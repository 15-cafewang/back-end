package com.sparta.backend.service.user;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.sparta.backend.awsS3.S3Uploader;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.domain.user.UserRole;
import com.sparta.backend.dto.request.user.DeleteUserRequestDto;
import com.sparta.backend.dto.request.user.SignupRequestDto;
import com.sparta.backend.dto.request.user.UpdateNicknameRequestDto;
import com.sparta.backend.dto.request.user.UpdateUserRequestDto;
import com.sparta.backend.dto.response.user.GetUserInfoResponseDto;
import com.sparta.backend.repository.user.UserRepository;
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
    public User registerUser(SignupRequestDto requestDto) {

        String email = requestDto.getEmail();

        String password = requestDto.getPassword();

        String passwordCheck = requestDto.getPasswordCheck();

        if( !passwordCheck.equals(password)){
            throw new IllegalArgumentException ("비밀번호가 일치하지 않습니다.");
        }

        password = passwordEncoder.encode(password);

        String nickname = requestDto.getNickname();

        String image = "https://user-images.githubusercontent.com/76515226/140890775-30641b72-226a-4068-8a0a-9a306e8c68b4.png";

        User user = new User(email, password, nickname, image, UserRole.USER, "Y");

        return userRepository.save(user);
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

    // 회원 정보 수정(닉네임만)
    @Transactional
    public void updateNickname(UserDetailsImpl userDetails, UpdateNicknameRequestDto requestDto) {

        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new NullPointerException("존재하지 않는 회원입니다")
        );

        if (!requestDto.getNickname().equals(user.getNickname())) {

            Optional<User> foundNickname = userRepository.findByNickname(requestDto.getNickname());

            if (foundNickname.isPresent()) {
                throw new IllegalArgumentException("이미 사용중인 닉네임 입니다");
            }
        }

        user.changeNickname(requestDto.getNickname());
    }

    // 회원 정보 수정
    @Transactional
    public void updateUser(UserDetailsImpl userDetails, UpdateUserRequestDto requestDto) throws IOException {

        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new NullPointerException("존재하지 않는 회원입니다")
        );

        // 닉네임을 변경하지 않아도 dto에 값이 들어오게 되는데 현재 닉네임과 다를 경우에만 수정
        if (!requestDto.getNickname().equals(user.getNickname())) {

            Optional<User> foundNickname = userRepository.findByNickname(requestDto.getNickname());

            if (foundNickname.isPresent()) {
                throw new IllegalArgumentException("이미 사용중인 닉네임 입니다");
            }
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

        return Pattern
                .matches("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", str);
    }

    // 닉네임 검사
    public boolean isNickname(String str) {

        return Pattern
                .matches("^([0-9a-zA-Z가-힣]{2,8})$", str);
    }

}
