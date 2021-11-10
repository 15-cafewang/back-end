package com.sparta.backend.validator;

import java.util.regex.Pattern;

public class UserValidator {

    public static boolean validateEmail(String email) {

        if (!Pattern.matches("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", email)) {
            throw new IllegalArgumentException("이메일 형식이 아닙니다");
        }

        return true;
    }

    public static boolean validatePassword(String password) {

        if (!Pattern.matches("^((?=.*[0-9])(?=.*[a-zA-Z])(?=.*[\\W]).{8,20})$", password)) {
            throw new IllegalArgumentException("비밀번호는 영문 대,소문자와 숫자, 8자 ~ 20자의 비밀번호여야 합니다");
        }

        return true;
    }

    public static boolean validateNickname(String nickname) {

        if (!Pattern.matches("^([0-9a-zA-Z가-힣]{2,8})$", nickname)) {
            throw new IllegalArgumentException("닉네임은 특수문자를 포함하지 않은 2자 이상 8자 이하여야합니다");
        }

        return true;
    }
    
}
