package com.sparta.backend.domain.user;

import com.sparta.backend.domain.User;
import com.sparta.backend.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Nested
    @DisplayName("회원 객체 생성")
    class CreateUser {

        @Nested
        @DisplayName("성공 케이스")
        class UserSuccess {

            @Test
            void userTest() {

                // given
                String email = "aaa@aaa.com";
                String password = "qweQWE123!@#";
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

        @Nested
        @DisplayName("실패 케이스")
        class UserFail {

            @Test
            @DisplayName("이메일 형식이 잘못된 경우")
            void emailError() {

                // given
                String email = "aaa";
                String password = "qweQWE123!@#";
                String nickname = "test";
                String image = "image";
                UserRole role = UserRole.USER;
                String status = "Y";

                // when then
                assertThatThrownBy(() -> {
                    new User(email, password, nickname, image, role, status);
                }).isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("이메일 형식이 아닙니다");
            }

            @Test
            @DisplayName("비밀번호 형식이 잘못된 경우")
            void passwordError() {

                // given
                String email = "aaa@aaa.com";
                String password = "qwe123";
                String nickname = "test";
                String image = "image";
                UserRole role = UserRole.USER;
                String status = "Y";

                // when then
                assertThatThrownBy(() -> {
                    new User(email, password, nickname, image, role, status);
                }).isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("비밀번호는 영문 대,소문자와 숫자, 8자 ~ 20자의 비밀번호여야 합니다");
            }

            @Test
            @DisplayName("닉네임 형식이 잘못된 경우")
            void nicknameError() {

                // given
                String email = "aaa@aaa.com";
                String password = "qweQWE123!@#";
                String nickname = "tester@@";
                String image = "image";
                UserRole role = UserRole.USER;
                String status = "Y";

                // when then
                assertThatThrownBy(() -> {
                    new User(email, password, nickname, image, role, status);
                }).isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("닉네임은 특수문자를 포함하지 않은 2자 이상 8자 이하여야합니다");
            }
        }
    }

}
