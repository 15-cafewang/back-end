package com.sparta.backend.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sparta.backend.validator.UserValidator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserValidatorTest {

    @Nested
    @DisplayName("이메일 검증 테스트")
    class EmailTest {

        @Test
        @DisplayName("성공 케이스")
        void emailSuccess() {

            // given
            String email = "aaa@naver.com";

            // when then
            assertThat(validateEmail(email)).isTrue();
        }

        @Nested
        @DisplayName("실패 케이스")
        class EmailFail {

            @Test
            @DisplayName("입력값이 빈 문자열인 경우")
            void emailFail_void() {

                // given
                String email = "";

                // when then
                assertThatThrownBy(() -> {
                    validateEmail(email);
                }).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("@가 포함되지 않은 경우")
            void emailFail_notIncludeAt() {

                // given
                String email = "asd123";

                // when then
                assertThatThrownBy(() -> {
                    validateEmail(email);
                }).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName(".이 포함되지 않은 경우")
            void emailFail_notIncludeDot() {

                // given
                String email = "asd123@asd";

                // when then
                assertThatThrownBy(() -> {
                    validateEmail(email);
                }).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName(".뒤에 두 글자 이상이 아닌 경우")
            void emailFail_notEnoughAfterDot() {

                // given
                String email = "asd123@asd.c";

                // when then
                assertThatThrownBy(() -> {
                    validateEmail(email);
                }).isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @Nested
    @DisplayName("비밀번호 검증 테스트")
    class PasswordTest {

        @Test
        @DisplayName("성공 테스트")
        void passwordSuccess() {

            // given
            String password = "qweQWE123!@#";

            // when then
            assertThat(validatePassword(password)).isTrue();
        }

        @Nested
        @DisplayName("실패 케이스")
        class PasswordFail {

            @Test
            @DisplayName("입력값이 빈 문자열일 경우")
            void passwordFail_void() {

                // given
                String password = "";

                // when then
                assertThatThrownBy(() -> {
                    validatePassword(password);
                }).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("길이가 8보다 작은 경우")
            void passwordFail_lessThan8() {

                // given
                String password = "qweQW1!";

                // when then
                assertThatThrownBy(() -> {
                    validatePassword(password);
                }).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("길이가 20보다 큰 경우")
            void passwordFail_moreThan20() {

                // given
                String password = "qqwweeQQWWEE112233!!@";

                // when then
                assertThatThrownBy(() -> {
                    validatePassword(password);
                }).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("숫자가 포함되지 않은 경우")
            void passwordFail_withoutDigit() {

                // given
                String password = "qweQWE!@#";

                // when then
                assertThatThrownBy(() -> {
                    validatePassword(password);
                }).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("영문 소문자가 포함되지 않은 경우")
            void passwordFail_withoutLowerCase() {

                // given
                String password = "QWE123!@#";

                // when then
                assertThatThrownBy(() -> {
                    validatePassword(password);
                }).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("영문 대문자가 포함되지 않은 경우")
            void passwordFail_withoutUpperCase() {

                // given
                String password = "qwe123!@#";

                // when then
                assertThatThrownBy(() -> {
                    validatePassword(password);
                }).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("특수문자가 포함되지 않은 경우")
            void passwordFail_withoutSpecialSymbol  () {

                // given
                String password = "qweQWE123";

                // when then
                assertThatThrownBy(() -> {
                    validatePassword(password);
                }).isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @Nested
    @DisplayName("닉네임 검증 테스트")
    class NicknameTest {

        @Nested
        @DisplayName("성공 케이스")
        class NicknameSuccess {

            @Test
            @DisplayName("숫자만 사용하는 경우")
            void nicknameSuccess_onlyDigit() {

                // given
                String nickname = "12345678";

                // when then
                assertThat(validateNickname(nickname)).isTrue();
            }

            @Test
            @DisplayName("영문만 사용하는 경우")
            void nicknameSuccess_onlyEnglish() {

                // given
                String nickname = "nickName";

                // when then
                assertThat(validateNickname(nickname)).isTrue();
            }

            @Test
            @DisplayName("한글만 사용하는 경우")
            void nicknameSuccess_onlyKorean() {

                // given
                String nickname = "한글도가능";

                // when then
                assertThat(validateNickname(nickname)).isTrue();
            }

            @Test
            @DisplayName("복합으로 사용하는 경우")
            void nicknameSuccess_mixed() {

                // given
                String nickname = "qW무야호123";

                // when then
                assertThat(validateNickname(nickname)).isTrue();
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class NicknameFail {

            @Test
            @DisplayName("입력값이 빈 문자열인 경우")
            void nicknameFail_void() {

                // given
                String nickname = "";

                // when then
                assertThatThrownBy(() -> {
                    validateNickname(nickname);
                }).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("길이가 2보다 작은 경우")
            void nicknameFail_lessThan2() {

                // given
                String nickname = "1";

                // when then
                assertThatThrownBy(() -> {
                    validateNickname(nickname);
                }).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("길이가 8보다 큰 경우")
            void nicknameFail_moreThan8() {

                // given
                String nickname = "asdqwe121";

                // when then
                assertThatThrownBy(() -> {
                    validateNickname(nickname);
                }).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("특수문자를 포함한 경우")
            void nicknameFail_withSpecialSymbol() {

                // given
                String nickname = "qwe123@";

                // when then
                assertThatThrownBy(() -> {
                    validateNickname(nickname);
                }).isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

}