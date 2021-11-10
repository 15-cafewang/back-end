package com.sparta.backend.validator;

import java.util.regex.Pattern;

public class UserValidator {

    public static boolean validateEmail(String email) {

        if (!Pattern.matches("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", email)) {
            throw new IllegalArgumentException("이메일 형식이 아닙니다");
        }

        return true;
    }
    
}
