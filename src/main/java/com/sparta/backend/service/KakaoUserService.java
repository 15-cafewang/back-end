package com.sparta.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.backend.domain.User;
import com.sparta.backend.domain.UserRole;
import com.sparta.backend.dto.request.user.KakaoUserInfoDto;
import com.sparta.backend.repository.UserRepository;
import com.sparta.backend.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class KakaoUserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public KakaoUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${kakao.client_id}")
    String clientId;

    public User kakaoLogin(String code) throws JsonProcessingException {

        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);

        // 2. "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 3. "카카오 사용자 정보"로 필요시 회원가입
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        // 4. 강제 로그인 처리
        forceLogin(kakaoUser);

        return kakaoUser;
    }

    private String getAccessToken(String code) throws JsonProcessingException {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", "http://localhost:3000/user/kakao/callback");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
//        String email = jsonNode.get("kakao_account")
//                .get("email").asText();

        return new KakaoUserInfoDto(id, nickname);
    }

    private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {

        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfo.getId();
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);
        if (kakaoUser == null) {
            // 회원가입
            // username: kakao nickname
            String nickname = kakaoUserInfo.getNickname();

            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            // email: kakao email
            String email = UUID.randomUUID().toString() + "@kakao.com";
            // role: 일반 사용자
//            UserRoleEnum role = UserRoleEnum.USER;

            String image = "https://99final.s3.ap-northeast-2.amazonaws.com/userImage/profile_image.png?response-content-disposition=inline&X-Amz-Security-Token=IQoJb3JpZ2luX2VjECcaDmFwLW5vcnRoZWFzdC0yIkYwRAIgY0Jq5HPHgI7BIgh2YNhq5t8lNDZLBkclAo4QD%2FFbvmICIA%2BxulZdxhspJ9FJL8uNB1GkCcOBehfhA0r11wg5NPOAKv8CCND%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEQABoMNjA2NDMwNjAxNjA4IgyK%2FOCfUwdlhErZCsgq0wLVqHCjFTplW7wVnT4jh7PrWXVTkZGztcNg2IjoDM34zsooGYwH1ZJE5aatc1nac41v0aX7Tgw5BrNkdDRQ2IQ3AW2Dh%2FJIJ7GxCQsIZxQwllHJgrhS3ajbmDYRZyCRotstGtRVkREqLnkBkbnkAgn%2FCXb0dVZLUh4NNsMwlk%2B4CKKFsTBxxf3IJU0gTkuOQ36qPLJyq6EMiQCAiINxNQgUp%2BIXYEaZJANtwi21l3eeww%2FwvdxAH07g%2B69ahcPK7bgOz9RYtNhP6ksCAcb56OOzpaDO5KQJ7n4Z0AFCmturAZUGPaUHnhNRFhZl6R9mpm7ASib76Vj7o0APYsZvCfSb4NLL9wWNBH%2FQEpkTYlhaFzUDjiljJCE4lr6LvR%2BgFqyTSa%2Fu8OZtYpPC%2Be946nlkEEP8e6G54HmlW4Xc2041eXFMgx%2FI5zthHZkO6J1mbieWBsIw96yojAY6tAIj9XwR%2FmH1UNrKXNMgoyaVg5Gwm0dwiI8j%2FkCdrmu0TAr8%2B9qVi%2B%2B5mwM7%2FLjY%2BQbHBI4sBwV7kL4WQhMPEx0mc8D5zOs6OdE2Jd83AyyVNI7o9Z1bwGK2XUUn5CUEMK9dyCToQXnAZlDuJrG0a%2B1bWLwBVRIAoJyVKxwVpnVo2%2BjSqyIjoPdoe%2BpZxic2L3MrR17CcjBZNB8lKJFt%2F0ODVn0aQq20EEiLCWQHCvM8HuKcE%2BAJrdzpxxS4030THDf2jq3DE07LgjOw%2B%2FdneQ15SdIBPRKSpd5xosDkGJcSnFasR3CbIYSvZPlMxL%2BC35j4RGLTPMWFR0LBRlfarXnVj9UaB%2Fp4EXFFa%2BVMkc7Wx48G0wQ38InJ9tEdEXvERTaTi3YJDvwvaPfzJ%2B5%2Fbj9UGk5OqA%3D%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20211109T063833Z&X-Amz-SignedHeaders=host&X-Amz-Expires=300&X-Amz-Credential=ASIAY2MQUUGEIQ2EL6HD%2F20211109%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=d9bf54c63e12193d87e3281e832aca317cbc821d241faebc8fc2e87051d4c86e";

            kakaoUser = new User(email, encodedPassword, nickname, image, UserRole.USER, kakaoId, "Y");
            userRepository.save(kakaoUser);
        }
        return kakaoUser;
    }

    private void forceLogin(User kakaoUser) {

        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
