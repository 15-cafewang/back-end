package com.sparta.backend.service.user;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.sparta.backend.awsS3.S3Uploader;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.domain.user.UserRole;
import com.sparta.backend.dto.request.user.SignupRequestDto;
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

import static com.sparta.backend.validator.UserValidator.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final S3Uploader s3Uploader;
    private final AmazonS3Client amazonS3Client;
    private final String bucket = "99final";

    public boolean validCheckEmail(String email) {

        validateEmail(email);

        Optional<User> foundUser = userRepository.findByEmail(email);

        return foundUser.isEmpty();
    }

    public boolean validCheckNickname(String nickname) {

        validateNickname(nickname);

        Optional<User> foundUser = userRepository.findByNickname(nickname);

        return foundUser.isEmpty();
    }

    public User registerUser(SignupRequestDto requestDto) {

        String email = requestDto.getEmail();

        String password = requestDto.getPassword();

        String passwordCheck = requestDto.getPasswordCheck();

        if( !passwordCheck.equals(password)){
            throw new IllegalArgumentException ("비밀번호가 일치하지 않습니다.");
        }

        password = passwordEncoder.encode(password);

        String nickname = requestDto.getNickname();

        String image = "https://user-images.githubusercontent.com/76515226/143576583-9b0bdb15-5e93-43d4-b328-445374f9f1ee.png";

        User user = new User(email, password, nickname, image, UserRole.USER, "Y");

        return userRepository.save(user);
    }

    public GetUserInfoResponseDto login(SignupRequestDto requestDto) {

        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new NullPointerException("이메일을 찾을 수 없습니다")
        );

        if (user.getStatus().equals("N")) throw new NullPointerException("존재하지 않는 회원입니다");

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 맞지 않습니다");
        }

        String token = jwtTokenProvider.createToken(requestDto.getEmail());
        String nickname = user.getNickname();
        String image = user.getImage();

        return new GetUserInfoResponseDto(token, nickname, image);
    }

    @Transactional
    public void updateUser(UserDetailsImpl userDetails, UpdateUserRequestDto requestDto) throws IOException {

        User user = getUser(userDetails);

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
    @Transactional
    public void deleteUser(UserDetailsImpl userDetails) {

        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new NullPointerException("존재하지 않는 회원입니다")
        );

        String email = UUID.randomUUID().toString();
        String nickname = UUID.randomUUID().toString();
        String image = "https://user-images.githubusercontent.com/76515226/143576583-9b0bdb15-5e93-43d4-b328-445374f9f1ee.png";

        user.deleteUser(email, nickname, image);
    }

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

    private User getUser(UserDetailsImpl userDetails) {
        return userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new NullPointerException("존재하지 않는 회원입니다")
        );
    }

}
