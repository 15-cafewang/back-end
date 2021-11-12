package com.sparta.backend.domain;

import com.sparta.backend.dto.request.board.PostBoardCommentRequestDto;
import com.sparta.backend.dto.request.board.PostBoardRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sparta.backend.domain.UserRole.USER;
import static org.junit.jupiter.api.Assertions.*;

class BoardCommentTest {
    @Nested
    @DisplayName("사용자가 request한 값으로 객체 생성")
    class CreateBoardComment {
        private String content;
        private User user;
        private Board board;

        private User loginUser;

        @BeforeEach
        void setup() {
            content = "전 누워서 유투브 보고 있었어요";
            //작성자
            String profile =
                    "https://user-images.githubusercontent.com/76515226/140890775-30641b72-226a-4068-8a0a-9a306e8c68b4.png";
            user = new User(1L, "aaa@aaa.com", "abab1234!",
                    "nao", profile, USER, "Y");

            PostBoardRequestDto requestDto =
                    new PostBoardRequestDto("다들 뭐하세용?", "저는 스타벅스에서 자몽 허니 블랙티 먹고 있어요!", null);
            board = new Board(1L, requestDto, user);

            //현재 로그인한 사용자
            loginUser = new User(1L, "aaa@aaa.com", "abab1234!",
                    "nao", profile, USER, "Y");
        }

        @Test
        @DisplayName("성공 케이스")
        void createBoardCommentNormal() {
            /* given */
            PostBoardCommentRequestDto requestDto =
                    new PostBoardCommentRequestDto(1L, content);

            /* when */
            BoardComment boardComment = new BoardComment(requestDto, board, user);

            /* then */
            assertEquals(content, boardComment.getContent());
            assertEquals(user, boardComment.getUser());
            assertEquals(board, boardComment.getBoard());

        }
    }

}