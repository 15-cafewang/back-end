package com.sparta.backend.validator;

import com.sparta.backend.domain.user.User;

public class CafeValidator {
    public static void validateCafeInput(String title, String content, String location, User user){
        if(user == null){
            throw  new IllegalArgumentException("로그인 되지 않은 사용자입니다");
        }
        if(user.getId() == null || user.getId()<0){
            throw new IllegalArgumentException("회원 id가 유효하지 않습니다.");
        }

        if(title == null || title.isEmpty()){
            throw new IllegalArgumentException("제목이 입력되지 않았습니다.");
        }

        if(title.length() > 100){
            throw new IllegalArgumentException("제목의 길이가 100자를 초과하였습니다.");
        }

        if(content.length()>1000){
            throw new IllegalArgumentException("내용의 길이가 1000자를 초과하였습니다");
        }

        if( location.length() > 100){
            throw new IllegalArgumentException("장소의 길이가 100자를 초과하였습니다.");
        }

    }
}
