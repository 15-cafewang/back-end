package com.sparta.backend.service;

import com.sparta.backend.domain.User;
import com.sparta.backend.domain.UserRole;
import com.sparta.backend.dto.request.user.SignupRequestDto;
import com.sparta.backend.repository.UserRepository;
import com.sparta.backend.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

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

        User user = new User(email, password, nickname, UserRole.USER, "Y");

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
}
