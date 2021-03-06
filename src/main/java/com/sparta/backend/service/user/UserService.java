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
            throw new IllegalArgumentException ("??????????????? ???????????? ????????????.");
        }

        password = passwordEncoder.encode(password);

        String nickname = requestDto.getNickname();

        String image = "https://user-images.githubusercontent.com/76515226/143576583-9b0bdb15-5e93-43d4-b328-445374f9f1ee.png";

        User user = new User(email, password, nickname, image, UserRole.USER, "Y");

        return userRepository.save(user);
    }

    public GetUserInfoResponseDto login(SignupRequestDto requestDto) {

        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new NullPointerException("???????????? ?????? ??? ????????????")
        );

        if (user.getStatus().equals("N")) throw new NullPointerException("???????????? ?????? ???????????????");

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("??????????????? ?????? ????????????");
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
                throw new IllegalArgumentException("?????? ???????????? ????????? ?????????");
            }
        }

        String imageUrl = user.getImage();

        if (requestDto.getImage() != null) {

            deleteS3(user.getImage());

            imageUrl = s3Uploader.upload(requestDto.getImage(), "userImage");

            if (imageUrl == null) throw new IllegalArgumentException("????????? ???????????? ?????????????????????");
        }

        user.changeProfile(requestDto.getNickname(), imageUrl);
    }

    // ?????? ??????
    @Transactional
    public void deleteUser(UserDetailsImpl userDetails) {

        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new NullPointerException("???????????? ?????? ???????????????")
        );

        String email = UUID.randomUUID().toString();
        String nickname = UUID.randomUUID().toString();
        String image = "https://user-images.githubusercontent.com/76515226/143576583-9b0bdb15-5e93-43d4-b328-445374f9f1ee.png";

        user.deleteUser(email, nickname, image);
    }

    public void deleteS3(@RequestParam String imageName){

        //https://S3 ?????? URL/????????? ????????? ?????????/???????????????
        String keyName = imageName.split("/")[4]; // ?????????????????? ??????

        try {
            amazonS3Client.deleteObject(bucket + "/userImage", keyName);
        }catch (AmazonServiceException e){
            e.printStackTrace();
            throw new AmazonServiceException(e.getMessage());
        }
    }

    private User getUser(UserDetailsImpl userDetails) {
        return userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new NullPointerException("???????????? ?????? ???????????????")
        );
    }

}
