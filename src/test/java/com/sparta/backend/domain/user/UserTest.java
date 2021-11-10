package com.sparta.backend.domain.user;

import com.sparta.backend.domain.User;
import com.sparta.backend.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("회원 객체 생성")
    void testUser() {

        // given
        String email = "aaa@aaa.com";
        String password = "1234";
        String nickname = "test";
        String image = "image";
        UserRole role = UserRole.USER;
        String status = "Y";

        // when
        User user = new User(email, password, nickname, image, role, status);

        // then
        assertThat(email).isEqualTo(user.getEmail());
        assertThat(password).isEqualTo(user.getPassword());
        assertThat(nickname).isEqualTo(user.getNickname());
        assertThat(image).isEqualTo(user.getImage());
        assertThat(role).isEqualTo(user.getRole());
        assertThat(status).isEqualTo(user.getStatus());
    }

}
