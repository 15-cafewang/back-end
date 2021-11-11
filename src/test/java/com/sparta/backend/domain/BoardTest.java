package com.sparta.backend.domain;

import com.sparta.backend.dto.request.board.PostBoardRequestDto;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

import static com.sparta.backend.domain.UserRole.USER;
import static org.junit.jupiter.api.Assertions.*;

//Board 엔티티 테스트
class BoardTest {

    @Nested
    @DisplayName("사용자가 request한 값으로 객체 생성")
    class CreateBoard {
        private String title;
        private String content;
        private MultipartFile[] image;

        private User loginUser;

        @BeforeEach
        void setup() throws IOException {
            title = "다들 뭐하고 계세요?!!";
            content = "저는 스타벅스 와서 허니 자몽 블랙티 먹고 있어요!";
            //S3에 upload하는 과정은 생략. 단지 이미지 파일을 입력했다고 가정할 뿐
            MockMultipartFile file1 = new MockMultipartFile(
                    "image", "choonsik1.png",
                    "multipart/form-data",
                    new FileInputStream("src/main/resources/static/testImage/choonsik1.png")
            );
            image = new MultipartFile[]{file1};

            //로그인한 계정
            String profile =
                    "https://user-images.githubusercontent.com/76515226/140890775-30641b72-226a-4068-8a0a-9a306e8c68b4.png";
            loginUser = new User(1L, "aaa@aaa.com", "abab1234!", "nao", profile, USER, "Y");
        }

        @Test
        @DisplayName("성공 케이스")
        void createBoardNormal() {
            /* given */
            //로그인한 사용자가 게시물 작성했다고 가정. validation으로 테스트했다고 가정
            PostBoardRequestDto requestDto =
                    new PostBoardRequestDto(title, content, image);

            /* when */
            //게시물 작성 시 입력한 값들로 게시물 작성 시 insert되는 엔티티 테스트
            Board board = new Board(requestDto, loginUser);

            /* then */
            //board
            assertEquals(title, board.getTitle());
            assertEquals(content, board.getContent());
            assertEquals(loginUser, board.getUser());

        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCases {
            @Nested
            @DisplayName("게시물 id")
            class BoardId {
                @Test
                @DisplayName("null")
                void fail1() {
                    /* given */
                    Long boardId = null;
                    PostBoardRequestDto requestDto =
                            new PostBoardRequestDto(title, content, image);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        Board board = new Board(boardId, requestDto, loginUser);
                    });

                    /* then */
                    assertEquals("존재하지 않는 게시물입니다.", exception.getMessage());
                }

                @Test
                @DisplayName("0")
                void fail2() {
                    /* given */
                    Long boardId = 0L;
                    PostBoardRequestDto requestDto =
                            new PostBoardRequestDto(title, content, image);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        Board board = new Board(boardId, requestDto, loginUser);
                    });

                    /* then */
                    assertEquals("존재하지 않는 게시물입니다.", exception.getMessage());
                }

                @Test
                @DisplayName("음수")
                void fail3() {
                    /* given */
                    Long boardId = -1L;
                    PostBoardRequestDto requestDto =
                            new PostBoardRequestDto(title, content, image);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        Board board = new Board(boardId, requestDto, loginUser);
                    });

                    /* then */
                    assertEquals("존재하지 않는 게시물입니다.", exception.getMessage());
                }
            }

            @Nested
            @DisplayName("제목")
            class Title {
                @Test
                @DisplayName("null")
                void fail1() {
                    /* given */
                    title = null;
                    PostBoardRequestDto requestDto =
                            new PostBoardRequestDto(title, content, image);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        Board board = new Board(requestDto, loginUser);
                    });

                    /* then */
                    assertEquals("제목을 입력해주세요.", exception.getMessage());
                }

                @Test
                @DisplayName("길이가 0 이하")
                void fail2() {
                    /* given */
                    title = "";
                    PostBoardRequestDto requestDto =
                            new PostBoardRequestDto(title, content, image);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        Board board = new Board(requestDto, loginUser);
                    });

                    /* then */
                    assertEquals("제목을 입력해주세요.", exception.getMessage());
                }

                @Test
                @DisplayName("길이가 기준보다 클 때")
                void fail3() {
                    /* given */
                    title = "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                            "안녕하세요안녕하세요";
                    PostBoardRequestDto requestDto =
                            new PostBoardRequestDto(title, content, image);

                    /* when */
                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        Board board = new Board(requestDto, loginUser);
                    });

                    /* then */
                    assertEquals("제목은 최대 100글자 입력 가능합니다.", exception.getMessage());
                }
            }

            @Nested
            @DisplayName("내용")
            class Content {
                @Test
                @DisplayName("null")
                void fail1() {
                    /* given */
                    content = null;
                    PostBoardRequestDto requestDto =
                            new PostBoardRequestDto(title, content, image);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        Board board = new Board(requestDto, loginUser);
                    });

                    /* then */
                    assertEquals("내용을 입력해주세요.", exception.getMessage());
                }

                @Test
                @DisplayName("길이가 0 이하")
                void fail2() {
                    /* given */
                    content = "";
                    PostBoardRequestDto requestDto =
                            new PostBoardRequestDto(title, content, image);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        Board board = new Board(requestDto, loginUser);
                    });

                    /* then */
                    assertEquals("내용을 입력해주세요.", exception.getMessage());
                }

                @Test
                @DisplayName("길이가 기준보다 클 때")
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
                    PostBoardRequestDto requestDto =
                            new PostBoardRequestDto(title, content, image);

                    /* when */
                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        Board board = new Board(requestDto, loginUser);
                    });

                    /* then */
                    assertEquals("내용은 최대 1000글자 입력 가능합니다.", exception.getMessage());
                }
            }

            @Nested
            @DisplayName("사용자")
            class User {
                @Test
                @DisplayName("null")
                void fail1() {
                    /* given */
                    loginUser = null;
                    PostBoardRequestDto requestDto =
                            new PostBoardRequestDto(title, content, image);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        Board board = new Board(requestDto, loginUser);
                    });

                    /* then */
                    assertEquals("로그인이 필요합니다.", exception.getMessage());
                }

                @Test
                @DisplayName("userId 길이가 0")
                void fail2() {
                    /* given */
                    String profile =
                            "https://user-images.githubusercontent.com/76515226/140890775-30641b72-226a-4068-8a0a-9a306e8c68b4.png";
                    loginUser =
                            new com.sparta.backend.domain.User(0L, "aaa@aaa.com", "abab1234!", "nao", profile, USER, "Y");
                    PostBoardRequestDto requestDto =
                            new PostBoardRequestDto(title, content, image);

                    /* when */
                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        Board board = new Board(requestDto, loginUser);
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
                    loginUser =
                            new com.sparta.backend.domain.User(null, "aaa@aaa.com", "abab1234!", "nao", profile, USER, "Y");
                    PostBoardRequestDto requestDto =
                            new PostBoardRequestDto(title, content, image);

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        Board board = new Board(requestDto, loginUser);
                    });

                    /* then */
                    assertEquals("로그인이 필요합니다.", exception.getMessage());
                }
            }
        }
    }
}