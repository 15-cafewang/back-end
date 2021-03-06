package com.sparta.backend.domain;

import com.sparta.backend.domain.board.Board;
import com.sparta.backend.domain.board.BoardComment;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.dto.request.board.PostBoardCommentRequestDto;
import com.sparta.backend.dto.request.board.PostBoardRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sparta.backend.domain.user.UserRole.USER;
import static org.junit.jupiter.api.Assertions.*;

class BoardCommentTest {
    @Nested
    @DisplayName("사용자가 request한 값으로 객체 생성")
    class CreateBoardComment {
        private String content;
        private User boardWriteUser;
        private Board board;

        private User loginUser;

        @BeforeEach
        void setup() {
            content = "전 누워서 유투브 보고 있었어요";
            //작성자
            String profile =
                    "https://user-images.githubusercontent.com/76515226/140890775-30641b72-226a-4068-8a0a-9a306e8c68b4.png";
            boardWriteUser = new User(1L, "aaa@aaa.com", "abab1234!",
                    "nao", profile, USER, "Y");

            PostBoardRequestDto requestDto =
                    new PostBoardRequestDto("다들 뭐하세용?", "저는 스타벅스에서 자몽 허니 블랙티 먹고 있어요!", null);
            board = new Board(1L, requestDto, boardWriteUser);

            //현재 로그인한 사용자
            loginUser = new User(1L, "bbb@bbb.com", "abab1234!",
                    "nao", profile, USER, "Y");
        }

        @Test
        @DisplayName("성공 케이스")
        void createBoardCommentNormal() {
            /* given */
            PostBoardCommentRequestDto requestDto =
                    new PostBoardCommentRequestDto(1L, content);

            /* when */
            BoardComment boardComment = new BoardComment(requestDto, board, loginUser);

            /* then */
            assertEquals(content, boardComment.getContent());
            assertEquals(loginUser, boardComment.getUser());
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
                    board = new Board(1L, boardRequestDto, boardWriteUser);
                    PostBoardCommentRequestDto commentRequestDto =
                            new PostBoardCommentRequestDto(1L, content);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        BoardComment boardComment = new BoardComment(commentId, commentRequestDto, board, loginUser);
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
                    board = new Board(1L, boardRequestDto, boardWriteUser);
                    PostBoardCommentRequestDto commentRequestDto =
                            new PostBoardCommentRequestDto(1L, content);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        BoardComment boardComment = new BoardComment(commentId, commentRequestDto, board, loginUser);
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
                    board = new Board(1L, boardRequestDto, boardWriteUser);
                    PostBoardCommentRequestDto commentRequestDto =
                            new PostBoardCommentRequestDto(1L, content);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        BoardComment boardComment = new BoardComment(commentId, commentRequestDto, board, loginUser);
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
                    board = new Board(1L, boardRequestDto, boardWriteUser);
                    PostBoardCommentRequestDto commentRequestDto =
                            new PostBoardCommentRequestDto(1L, content);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        BoardComment boardComment = new BoardComment(1L, commentRequestDto, board, loginUser);
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
                    board = new Board(1L, boardRequestDto, boardWriteUser);
                    PostBoardCommentRequestDto commentRequestDto =
                            new PostBoardCommentRequestDto(1L, content);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        BoardComment boardComment = new BoardComment(1L, commentRequestDto, board, loginUser);
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
                    board = new Board(1L, boardRequestDto, boardWriteUser);
                    PostBoardCommentRequestDto commentRequestDto =
                            new PostBoardCommentRequestDto(1L, content);

                    /* when */
                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        BoardComment boardComment = new BoardComment(1L, commentRequestDto, board, loginUser);
                    });

                    /* then */
                    assertEquals("내용은 최대 1000글자 입력 가능합니다.", exception.getMessage());

                }
            }

            @Nested
            @DisplayName("게시물")
            class BoardEntity {
                @Test
                @DisplayName("null")
                void fail1() {
                    /* given */
                    board = null;
                    PostBoardCommentRequestDto commentRequestDto =
                            new PostBoardCommentRequestDto(null, content);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        BoardComment boardComment = new BoardComment(commentRequestDto, board, loginUser);
                    });

                    /* then */
                    assertEquals("존재하지 않는 게시물입니다.", exception.getMessage());
                }

                @Test
                @DisplayName("boardId가 null")
                void fail2() {
                    /* given */
                    Long boardId = null;
                    PostBoardCommentRequestDto commentRequestDto =
                            new PostBoardCommentRequestDto(boardId, content);

                    PostBoardRequestDto requestDto =
                            new PostBoardRequestDto("제목","내용", null);


                    /* when */
                    //BoardComment 엔티티를 생성하기 전에
                    //Board 엔티티를 생성할 때부터 exception이 발생
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        Board board = new Board(boardId, requestDto, loginUser);
                        BoardComment boardComment = new BoardComment(commentRequestDto, board, loginUser);
                    });

                    /* then */
                    //Board 엔티티에서 발생하는 exception
                    assertEquals("존재하지 않는 게시물입니다.", exception.getMessage());
                }

                @Test
                @DisplayName("boardId가 0")
                void fail3() {
                    /* given */
                    Long boardId = 0L;
                    PostBoardCommentRequestDto commentRequestDto =
                            new PostBoardCommentRequestDto(boardId, content);

                    PostBoardRequestDto requestDto =
                            new PostBoardRequestDto("제목","내용", null);


                    /* when */
                    //BoardComment 엔티티를 생성하기 전에
                    //Board 엔티티를 생성할 때부터 exception이 발생
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        Board board = new Board(boardId, requestDto, loginUser);
                        BoardComment boardComment = new BoardComment(commentRequestDto, board, loginUser);
                    });

                    /* then */
                    //Board 엔티티에서 발생하는 exception
                    assertEquals("존재하지 않는 게시물입니다.", exception.getMessage());
                }

                @Test
                @DisplayName("boardId가 음수")
                void fail4() {
                    /* given */
                    Long boardId = -1L;
                    PostBoardCommentRequestDto commentRequestDto =
                            new PostBoardCommentRequestDto(boardId, content);

                    PostBoardRequestDto requestDto =
                            new PostBoardRequestDto("제목","내용", null);


                    /* when */
                    //BoardComment 엔티티를 생성하기 전에
                    //Board 엔티티를 생성할 때부터 exception이 발생
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        Board board = new Board(boardId, requestDto, loginUser);
                        BoardComment boardComment = new BoardComment(commentRequestDto, board, loginUser);
                    });

                    /* then */
                    //Board 엔티티에서 발생하는 exception
                    assertEquals("존재하지 않는 게시물입니다.", exception.getMessage());
                }
            }

            @Nested
            @DisplayName("사용자")
            class UserEntity {
                @Test
                @DisplayName("null")
                void fail1() {
                    /* given */
                    loginUser = null;
                    PostBoardCommentRequestDto requestDto =
                            new PostBoardCommentRequestDto(1L, content);


                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        BoardComment boardComment = new BoardComment(requestDto, board, loginUser);
                    });

                    /* then */
                    assertEquals("로그인이 필요합니다.", exception.getMessage());
                }

                @Test
                @DisplayName("userId가 0")
                void fail2() {
                    /* given */
                    String profile =
                            "https://user-images.githubusercontent.com/76515226/140890775-30641b72-226a-4068-8a0a-9a306e8c68b4.png";
                    loginUser = new User(0L, "bbb@bbb.com", "abab1234!",
                            "nao", profile, USER, "Y");
                    PostBoardCommentRequestDto requestDto =
                            new PostBoardCommentRequestDto(1L, content);


                    /* when */
                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        BoardComment boardComment = new BoardComment(requestDto, board, loginUser);
                    });

                    /* then */
                    assertEquals("존재하지 않는 사용자입니다.", exception.getMessage());
                }

                @Test
                @DisplayName("userId가 null")
                void fail3() {
                    /* given */
                    String profile =
                            "https://user-images.githubusercontent.com/76515226/140890775-30641b72-226a-4068-8a0a-9a306e8c68b4.png";
                    loginUser = new User(null, "bbb@bbb.com", "abab1234!",
                            "nao", profile, USER, "Y");
                    PostBoardCommentRequestDto requestDto =
                            new PostBoardCommentRequestDto(1L, content);


                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        BoardComment boardComment = new BoardComment(requestDto, board, loginUser);
                    });

                    /* then */
                    assertEquals("존재하지 않는 사용자입니다.", exception.getMessage());
                }
            }
        }
    }

}