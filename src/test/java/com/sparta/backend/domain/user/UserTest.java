package com.sparta.backend.domain.user;

import com.sparta.backend.domain.User;
import com.sparta.backend.domain.UserRole;
import com.sparta.backend.validator.UserValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("이메일 검증 테스트")
    class EmailTest {

        @Test
        @DisplayName("성공 케이스")
        void emailSuccess() {

            // given
            String email = "aaa@naver.com";

            // when then
            assertThat(UserValidator.validateEmail(email)).isTrue();
        }

        @Nested
        @DisplayName("실패 케이스")
        class EmailFail {

            @Test
            @DisplayName("입력값이 null일 경우")
            void emailFail_null() {

                // given
                String email = "";

                // when then
                assertThatThrownBy(() -> {
                    UserValidator.validateEmail(email);
                }).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("@가 포함되지 않을경우")
            void emailFail_notIncludeAt() {

                // given
                String email = "asd123";

                // when then
                assertThatThrownBy(() -> {
                    UserValidator.validateEmail(email);
                }).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName(".이 포함되지 않을경우")
            void emailFail_notIncludeDot() {

                // given
                String email = "asd123@asd";

                // when then
                assertThatThrownBy(() -> {
                    UserValidator.validateEmail(email);
                }).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName(".뒤에 두글자 이상이 아닌경우")
            void emailFail_notEnoughAfterDot() {

                // given
                String email = "asd123@asd.c";

                // when then
                assertThatThrownBy(() -> {
                    UserValidator.validateEmail(email);
                }).isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

}
