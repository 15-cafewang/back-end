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

        if (!Pattern.matches("^((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W]).{8,20})$", password)) {
            throw new IllegalArgumentException("비밀번호는 숫자, 영문 소문자, 대문자, 특수문자를 하나씩 포함한 8자이상 20자 이하여야합니다");
        }

        return true;
    }
    
}
