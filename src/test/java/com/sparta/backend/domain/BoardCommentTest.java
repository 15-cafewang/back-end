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

        @Nested
        @DisplayName("실패 케이스")
        class FailCases {
            @Nested
            @DisplayName("댓글 id")
            class BoardCommentId {
                @Test
                @DisplayName("null")
                void fail1() {
                    /* given */
                    Long commentId = null;
                    PostBoardRequestDto boardRequestDto = new PostBoardRequestDto("제목", "내용", null);
                    board = new Board(1L, boardRequestDto, user);
                    PostBoardCommentRequestDto commentRequestDto =
                            new PostBoardCommentRequestDto(1L, content);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        BoardComment boardComment = new BoardComment(commentId, commentRequestDto, board, user);
                    });

                    /* then */
                    assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());

                }

                @Test
                @DisplayName("0")
                void fail2() {
                    /* given */
                    Long commentId = 0L;
                    PostBoardRequestDto boardRequestDto = new PostBoardRequestDto("제목", "내용", null);
                    board = new Board(1L, boardRequestDto, user);
                    PostBoardCommentRequestDto commentRequestDto =
                            new PostBoardCommentRequestDto(1L, content);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        BoardComment boardComment = new BoardComment(commentId, commentRequestDto, board, user);
                    });

                    /* then */
                    assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());

                }

                @Test
                @DisplayName("음수")
                void fail3() {
                    /* given */
                    Long commentId = -1L;
                    PostBoardRequestDto boardRequestDto = new PostBoardRequestDto("제목", "내용", null);
                    board = new Board(1L, boardRequestDto, user);
                    PostBoardCommentRequestDto commentRequestDto =
                            new PostBoardCommentRequestDto(1L, content);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        BoardComment boardComment = new BoardComment(commentId, commentRequestDto, board, user);
                    });

                    /* then */
                    assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());

                }
            }

            @Nested
            @DisplayName("댓글 내용")
            class BoardContent {
                @Test
                @DisplayName("null")
                void fail1() {
                    /* given */
                    content = null;
                    PostBoardRequestDto boardRequestDto = new PostBoardRequestDto("제목", "내용", null);
                    board = new Board(1L, boardRequestDto, user);
                    PostBoardCommentRequestDto commentRequestDto =
                            new PostBoardCommentRequestDto(1L, content);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        BoardComment boardComment = new BoardComment(1L, commentRequestDto, board, user);
                    });

                    /* then */
                    assertEquals("내용을 입력하세요", exception.getMessage());

                }

                @Test
                @DisplayName("길이가 0 이하")
                void fail2() {
                    /* given */
                    content = "";
                    PostBoardRequestDto boardRequestDto = new PostBoardRequestDto("제목", "내용", null);
                    board = new Board(1L, boardRequestDto, user);
                    PostBoardCommentRequestDto commentRequestDto =
                            new PostBoardCommentRequestDto(1L, content);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        BoardComment boardComment = new BoardComment(1L, commentRequestDto, board, user);
                    });

                    /* then */
                    assertEquals("내용을 입력하세요", exception.getMessage());

                }

                @Test
                @DisplayName("길이가 기준보다 클 떄")
                void fail3() {
                    /* given */
                    content = "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요";
                    PostBoardRequestDto boardRequestDto = new PostBoardRequestDto("제목", "내용", null);
                    board = new Board(1L, boardRequestDto, user);
                    PostBoardCommentRequestDto commentRequestDto =
                            new PostBoardCommentRequestDto(1L, content);

                    /* when */
                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        BoardComment boardComment = new BoardComment(1L, commentRequestDto, board, user);
                    });

                    /* then */
                    assertEquals("내용은 최대 1000글자 입력 가능합니다.", exception.getMessage());

                }
            }
        }
    }

}