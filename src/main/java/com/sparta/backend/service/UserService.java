package com.sparta.backend.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.sparta.backend.awsS3.S3Uploader;
import com.sparta.backend.domain.User;
import com.sparta.backend.domain.UserRole;
import com.sparta.backend.dto.request.user.SignupRequestDto;
import com.sparta.backend.dto.request.user.UpdateRequestDto;
import com.sparta.backend.repository.UserRepository;
import com.sparta.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final S3Uploader s3Uploader;
    private final AmazonS3Client amazonS3Client;
    private final String bucket = "99final";

    //회원등록
    public void registerUser(SignupRequestDto requestDto) {

        String email = requestDto.getEmail();

        Optional<User> found = userRepository.findByEmail(email);

        if (found.isPresent()) {
            throw new IllegalArgumentException("중복된 이메일이 존재합니다");
        }

        String password = requestDto.getPassword();

        String passwordCheck =requestDto.getPasswordCheck();

        if( !passwordCheck.equals(password)){
            throw new IllegalArgumentException ("비밀번호가 일치하지 않습니다.");
        }

        password = passwordEncoder.encode(password);

        String nickname = requestDto.getNickname();

        User user = new User(email, password, nickname, null, UserRole.USER, "Y");

        userRepository.save(user);
    }


    public List<Map<String, String>> login(SignupRequestDto requestDto) {

        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("아이디를 찾을수 없습니다")
        );

        if (!passwordEncoder.matches(requestDto.getPassword(),user.getPassword())) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }

        Map<String, String> nickname = new HashMap<>();
        Map<String, String> token = new HashMap<>();
        List<Map<String, String>>  tu = new ArrayList<>();
        token.put("Authorization", jwtTokenProvider.createToken(requestDto.getEmail()));
        nickname.put("nickname", user.getNickname());
        tu.add(nickname);
        tu.add(token);

        return tu;
    }

    // 회원 정보 수정
    public void updateUser(Long userId, UpdateRequestDto requestDto) throws IOException {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 회원입니다")
        );

        String imageUrl = user.getImage();

        if (requestDto.getImage() != null) {

            User foundUser = userRepository.findById(userId).orElseThrow(
                    () -> new NullPointerException("존재하지 않는 회원입니다")
            );

            deleteS3(foundUser.getImage());

            imageUrl = s3Uploader.upload(requestDto.getImage(), "userImage");

            if (imageUrl == null) throw new IllegalArgumentException("이미지 업로드에 실패하였습니다");
        }

        user.changeProfile(requestDto.getNickname(), imageUrl);
    }

    //S3 이미지 삭제
    public void deleteS3(@RequestParam String imageName){
        //https://S3 버킷 URL/버킷에 생성한 폴더명/이미지이름
        String keyName = imageName.split("/")[4]; // 이미지이름만 추출

        try {amazonS3Client.deleteObject(bucket + "/userImage", keyName);
        }catch (AmazonServiceException e){
            e.printStackTrace();
            throw new AmazonServiceException(e.getMessage());
        }
    }
}
