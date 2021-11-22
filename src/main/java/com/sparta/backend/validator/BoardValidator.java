package com.sparta.backend.validator;

import com.sparta.backend.domain.user.User;
import com.sparta.backend.dto.request.board.PostBoardRequestDto;

public class BoardValidator {
    public static void boardValidatorBoardId(Long boardId, PostBoardRequestDto requestDto, User user) {
        if(boardId == null || boardId <= 0) {
            throw new NullPointerException("존재하지 않는 게시물입니다.");
        }
        boardValidatorRequestDto(requestDto, user);
    }

    public static void boardValidatorRequestDto(PostBoardRequestDto requestDto, User user) {
        String title = requestDto.getTitle();
        String content = requestDto.getContent();
        boardValidator(title, content, user);
    }


    public static void boardValidator(String title, String content, User user) {
        //제목
        if(title == null) { //request에서 title 자체가 없을 때
            throw new NullPointerException("제목을 입력해주세요.");
        } else {    //request에서 title이 있을 때
            if(title.length() <= 0) {   //제목을 입력하지 않았을 때
                throw new NullPointerException("제목을 입력해주세요.");
            }
            if(title.length() > 100) {  //제목이 기준을 넘겼을 때
                throw new IllegalArgumentException("제목은 최대 100글자 입력 가능합니다.");
            }
        }

        //내용
        if(content == null) {
            throw new NullPointerException("내용을 입력해주세요.");
        } else {
            if(content.length() <= 0) {
                throw new NullPointerException("내용을 입력해주세요.");
            }
            if(content.length() > 1000) {
                throw new IllegalArgumentException("내용은 최대 1000글자 입력 가능합니다.");
            }
        }

        //사용자
        if(user == null || user.getId() == null) {
            throw new NullPointerException("로그인이 필요합니다.");
        } else {
            if(user.getId() <= 0) {
                throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
            }
        }
    }
}
