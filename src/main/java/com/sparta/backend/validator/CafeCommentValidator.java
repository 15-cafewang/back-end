package com.sparta.backend.validator;

import com.sparta.backend.domain.cafe.Cafe;
import com.sparta.backend.domain.user.User;

public class CafeCommentValidator {

    public static void validateCommentInput(String content, User user, Cafe cafe) {
        //권한관련
        if(user == null){
            throw  new IllegalArgumentException("로그인 되지 않은 사용자입니다");
        }
        if(user.getId() == null || user.getId()<0){
            throw new IllegalArgumentException("회원 id가 유효하지 않습니다.");
        }
        //댓글 달 게시물 관련
        if(cafe == null || cafe.getId() <0){
            throw new IllegalArgumentException("댓글 달 게시물이 존재하지 않습니다.");
        }

        //내용관련
        if(content == null || content.isEmpty()){
            throw new IllegalArgumentException("댓글 내용이 입력되지 않았습니다.");
        }
        if(content.length() > 200){
            throw new IllegalArgumentException("댓글 내용이 200자를 초과하였습니다.");
        }
    }

    public static void validateCommentInput(String content) {
        if(content == null || content.isEmpty()){
            throw new IllegalArgumentException("댓글 내용이 입력되지 않았습니다.");
        }
        if(content.length() > 200){
            throw new IllegalArgumentException("댓글 내용이 200자를 초과하였습니다.");
        }
    }
}
