package com.sparta.backend.validator;

import com.sparta.backend.domain.Board;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.request.board.PostBoardCommentRequestDto;

public class BoardCommentValidator {
    public static void boardCommentValidatorId(Long commentId, PostBoardCommentRequestDto requestDto,
                                               Board board, User user) {
        if(commentId == null || commentId <= 0) {
            throw new NullPointerException("존재하지 않는 댓글입니다.");
        }
        boardCommentValidatorRequestDto(requestDto, board, user);
    }

    public static void boardCommentValidatorRequestDto(PostBoardCommentRequestDto requestDto,
                                                       Board board, User user) {
        String content = requestDto.getContent();
        boardCommentValidator(content, board, user);
    }

    public static void boardCommentValidator(String content, Board board, User user) {
        //내용
        if(content == null) {
            throw new NullPointerException("내용을 입력하세요");
        } else {
            if(content.length() <= 0) {
                throw new NullPointerException("내용을 입력하세요");
            } else if(content.length() > 1000) {
                throw new IllegalArgumentException("내용은 최대 1000글자 입력 가능합니다.");
            }
        }

        //게시물
        if(board == null) {
            throw new NullPointerException("존재하지 않는 게시물입니다.");
        }
        // Board 엔티티에서 아래와 같은 값이 들어온다면
        // BoardComment 엔티티에서 exception이 나기 전
        // BoardValidator에서 exception이 일어남
//        else {
//            if(board.getId() == null || board.getId() <= 0) {
//                throw new NullPointerException("존재하지 않는 게시물입니다");
//            }
//        }

        //사용자
        if(user == null) {
            throw new NullPointerException("로그인이 필요합니다.");
        } else {
            if(user.getId() == null) {
                throw new NullPointerException("존재하지 않는 사용자입니다.");
            } else if(user.getId() <= 0) {
                throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
            }
        }

    }
}
