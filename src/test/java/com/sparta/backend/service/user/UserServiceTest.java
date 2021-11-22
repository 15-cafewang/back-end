package com.sparta.backend.service.user;

import com.sparta.backend.domain.user.User;
import com.sparta.backend.domain.user.UserRole;
import com.sparta.backend.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.sparta.backend.validator.UserValidator.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupUser() {

        String email = "testUser@gmail.com";
        String password = passwordEncoder.encode("qweQWE123!@#");
        String nickname = "testUser";
        String image = "https://user-images.githubusercontent.com/76515226/140890775-30641b72-226a-4068-8a0a-9a306e8c68b4.png";
        UserRole role = UserRole.USER;
        String status = "Y";

        User user = new User(email, password, nickname, image, role, status);
        userRepository.save(user);
    }

    @Nested
    @DisplayName("이메일 중복 체크 테스트")
    class validCheckEmail {

        @Test
        @DisplayName("성공 케이스")
        void success() {

            // given
            String email = "user01@gmail.com";

            // when
            validateEmail(email);
            Optional<User> found = userRepository.findByEmail(email);

            // then
            Assertions.assertThat(found.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("실패 케이스")
        void fail() {

            // given
            String email = "testUser@gmail.com";

            // when
            validateEmail(email);
            Optional<User> found = userRepository.findByEmail(email);

            // then
            Assertions.assertThat(found.isEmpty()).isFalse();
        }
    }

}