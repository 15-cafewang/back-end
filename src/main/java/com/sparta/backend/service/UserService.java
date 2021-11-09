package com.sparta.backend.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.sparta.backend.awsS3.S3Uploader;
import com.sparta.backend.domain.User;
import com.sparta.backend.domain.UserRole;
import com.sparta.backend.dto.request.user.DeleteUserRequestDto;
import com.sparta.backend.dto.request.user.SignupRequestDto;
import com.sparta.backend.dto.request.user.UpdateUserRequestDto;
import com.sparta.backend.dto.response.user.GetUserInfoResponseDto;
import com.sparta.backend.repository.UserRepository;
import com.sparta.backend.security.JwtTokenProvider;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final S3Uploader s3Uploader;
    private final AmazonS3Client amazonS3Client;
    private final String bucket = "99final";

    // 이메일 중복 체크
    public int validCheckEmail(String email) {

        Optional<User> found = userRepository.findByEmail(email);

        if (found.isPresent()) return 1;
        if (!isEmail(email)) return 2;

        return 0;
    }

    // 닉네임 중복 체크
    public int validCheckNickname(String nickname) {

        Optional<User> found = userRepository.findByNickname(nickname);

        if (found.isPresent()) return 1;
        if (!isNickname(nickname)) return 2;

        return 0;
    }

    //회원등록
    public void registerUser(SignupRequestDto requestDto) {

        String email = requestDto.getEmail();

        String password = requestDto.getPassword();

        String passwordCheck = requestDto.getPasswordCheck();

        if( !passwordCheck.equals(password)){
            throw new IllegalArgumentException ("비밀번호가 일치하지 않습니다.");
        }

        password = passwordEncoder.encode(password);

        String nickname = requestDto.getNickname();

//        String image = "https://99final.s3.ap-northeast-2.amazonaws.com/userImage/profile_image.png?response-content-disposition=inline&X-Amz-Security-Token=IQoJb3JpZ2luX2VjECcaDmFwLW5vcnRoZWFzdC0yIkYwRAIgY0Jq5HPHgI7BIgh2YNhq5t8lNDZLBkclAo4QD%2FFbvmICIA%2BxulZdxhspJ9FJL8uNB1GkCcOBehfhA0r11wg5NPOAKv8CCND%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEQABoMNjA2NDMwNjAxNjA4IgyK%2FOCfUwdlhErZCsgq0wLVqHCjFTplW7wVnT4jh7PrWXVTkZGztcNg2IjoDM34zsooGYwH1ZJE5aatc1nac41v0aX7Tgw5BrNkdDRQ2IQ3AW2Dh%2FJIJ7GxCQsIZxQwllHJgrhS3ajbmDYRZyCRotstGtRVkREqLnkBkbnkAgn%2FCXb0dVZLUh4NNsMwlk%2B4CKKFsTBxxf3IJU0gTkuOQ36qPLJyq6EMiQCAiINxNQgUp%2BIXYEaZJANtwi21l3eeww%2FwvdxAH07g%2B69ahcPK7bgOz9RYtNhP6ksCAcb56OOzpaDO5KQJ7n4Z0AFCmturAZUGPaUHnhNRFhZl6R9mpm7ASib76Vj7o0APYsZvCfSb4NLL9wWNBH%2FQEpkTYlhaFzUDjiljJCE4lr6LvR%2BgFqyTSa%2Fu8OZtYpPC%2Be946nlkEEP8e6G54HmlW4Xc2041eXFMgx%2FI5zthHZkO6J1mbieWBsIw96yojAY6tAIj9XwR%2FmH1UNrKXNMgoyaVg5Gwm0dwiI8j%2FkCdrmu0TAr8%2B9qVi%2B%2B5mwM7%2FLjY%2BQbHBI4sBwV7kL4WQhMPEx0mc8D5zOs6OdE2Jd83AyyVNI7o9Z1bwGK2XUUn5CUEMK9dyCToQXnAZlDuJrG0a%2B1bWLwBVRIAoJyVKxwVpnVo2%2BjSqyIjoPdoe%2BpZxic2L3MrR17CcjBZNB8lKJFt%2F0ODVn0aQq20EEiLCWQHCvM8HuKcE%2BAJrdzpxxS4030THDf2jq3DE07LgjOw%2B%2FdneQ15SdIBPRKSpd5xosDkGJcSnFasR3CbIYSvZPlMxL%2BC35j4RGLTPMWFR0LBRlfarXnVj9UaB%2Fp4EXFFa%2BVMkc7Wx48G0wQ38InJ9tEdEXvERTaTi3YJDvwvaPfzJ%2B5%2Fbj9UGk5OqA%3D%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20211109T063833Z&X-Amz-SignedHeaders=host&X-Amz-Expires=300&X-Amz-Credential=ASIAY2MQUUGEIQ2EL6HD%2F20211109%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=d9bf54c63e12193d87e3281e832aca317cbc821d241faebc8fc2e87051d4c86e";
        String image = null;
        User user = new User(email, password, nickname, image, UserRole.USER, "Y");

        userRepository.save(user);
    }

    // 로그인
    public GetUserInfoResponseDto login(SignupRequestDto requestDto) {

        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new NullPointerException("아이디를 찾을수 없습니다")
        );

        if (user.getStatus().equals("N")) throw new NullPointerException("존재하지 않는 회원입니다");

        if (!passwordEncoder.matches(requestDto.getPassword(),user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 맞지 않습니다");
        }

        String token = jwtTokenProvider.createToken(requestDto.getEmail());
        String nickname = user.getNickname();
        String image = user.getImage();

        return new GetUserInfoResponseDto(token, nickname, image);
    }

    // 회원 정보 수정
    @Transactional
    public void updateUser(UserDetailsImpl userDetails, UpdateUserRequestDto requestDto) throws IOException {

        User user = userDetails.getUser();

        Optional<User> foundNickname = userRepository.findByNickname(requestDto.getNickname());

        if (foundNickname.isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 닉네임 입니다");
        }

        String imageUrl = user.getImage();

        if (requestDto.getImage() != null) {

            deleteS3(user.getImage());

            imageUrl = s3Uploader.upload(requestDto.getImage(), "userImage");

            if (imageUrl == null) throw new IllegalArgumentException("이미지 업로드에 실패하였습니다");
        }

        user.changeProfile(requestDto.getNickname(), imageUrl);
    }

    // 회원 탈퇴
    public void deleteUser(UserDetailsImpl userDetails, DeleteUserRequestDto requestDto) {

        User user = userDetails.getUser();

        if (!passwordEncoder.matches(requestDto.getPassword(),user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        user.deleteUser("N");
    }

    // S3 이미지 삭제
    public void deleteS3(@RequestParam String imageName){

        //https://S3 버킷 URL/버킷에 생성한 폴더명/이미지이름
        String keyName = imageName.split("/")[4]; // 이미지이름만 추출

        try {
            amazonS3Client.deleteObject(bucket + "/userImage", keyName);
        }catch (AmazonServiceException e){
            e.printStackTrace();
            throw new AmazonServiceException(e.getMessage());
        }
    }

    // 이메일 검사
    public boolean isEmail(String str) {

        return Pattern.matches("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", str);
    }

    // 닉네임 검사
    public boolean isNickname(String str) {

        return Pattern.matches("^([^\\W]{2,8})$", str);
    }

}
