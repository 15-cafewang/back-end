package com.sparta.backend.domain;

import com.sparta.backend.domain.User;
import com.sparta.backend.domain.constant.UserRole;
import com.sparta.backend.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class UserTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanup() {

        userRepository.deleteAll();
    }

    @Test
    void userInsert() {

        // given
        userRepository.save(
                User.builder()
                        .email("test@naver.com")
                        .password("1234")
                        .nickname("tester")
                        .role(UserRole.USER)
                        .build()
        );

        // when
        List<User> userList = userRepository.findAll();

        // then
        User user = userList.get(0);
        assertThat(user.getEmail()).isEqualTo("test@naver.com");
        assertThat(user.getPassword()).isEqualTo("1234");
        assertThat(user.getNickname()).isEqualTo("tester");
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
    }

}
