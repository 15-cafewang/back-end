package com.sparta.backend.domain;

import com.sparta.backend.dto.request.board.PostBoardRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

import static com.sparta.backend.domain.UserRole.USER;
import static org.junit.jupiter.api.Assertions.*;

class BoardImageTest {
    @Nested
    @DisplayName("사용자가 request한 값으로 객체 생성")
    class CreateBoardImage {
        private String image;
        private Board board;

        @BeforeEach
        void setup() throws IOException {
            //이미지 파일을 S3에 올려 이미지 URL이 생성되었다고 가정
            image = "https://99final.s3.ap-northeast-2.amazonaws.com/boardImage/836fe1eb-d4a0-4563-be62-26099f1b11e1choonsik2.png";

            MockMultipartFile file1 = new MockMultipartFile(
                    "image", "choonsik1.png",
                    "multipart/form-data",
                    new FileInputStream("src/test/java/com/sparta/backend/images/choonsik2.png")
            );
            MultipartFile[] boardImageFile = new MultipartFile[]{file1};
            PostBoardRequestDto boardRequestDto = new PostBoardRequestDto("제목", "내용", boardImageFile);

            String profile =
                    "https://user-images.githubusercontent.com/76515226/140890775-30641b72-226a-4068-8a0a-9a306e8c68b4.png";
            //게시물 작성자
            User boardWriteUser = new User(1L, "aaa@aaa.com", "abab1234!",
                                                            "nao", profile, USER, "Y");

            board = new Board(boardRequestDto, boardWriteUser);
        }

        @Test
        @DisplayName("성공 케이스")
        void createBoardImageNormal() {
            /* given */
            //이미지 파일을 S3에 올려 이미지 URL이 생성되었다고 가정(위에 있음)

            /* when */
            BoardImage boardImage = new BoardImage(image, board);

            /* then */
            assertEquals(image, boardImage.getImage());
            assertEquals(board, boardImage.getBoard());
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCases {
            @Nested
            @DisplayName("이미지 URL")
            class ImageUrl {
                @Test
                @DisplayName("null")
                void fail1() {
                    /* given */
                    image = null;

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        BoardImage boardImage = new BoardImage(image, board);
                    });

                    /* then */
                    assertEquals("이미지가 존재하지 않습니다.", exception.getMessage());
                }

                @Test
                @DisplayName("빈 문자열")
                void fail2() {
                    /* given */
                    image = "";

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        BoardImage boardImage = new BoardImage(image, board);
                    });

                    /* then */
                    assertEquals("이미지가 존재하지 않습니다.", exception.getMessage());
                }

                @Test
                @DisplayName("URL 형식이 아닐 경우")
                void fail3() {
                    /* given */
                    image = "99final.s3.ap-northeast-2.amazonaws.com/boardImage/836fe1eb-d4a0-4563-be62-26099f1b11e1choonsik2.png";

                    /* when */
                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        BoardImage boardImage = new BoardImage(image, board);
                    });

                    /* then */
                    assertEquals("URL 형식이 아닙니다.", exception.getMessage());
                }
            }

            @Nested
            @DisplayName("게시물")
            class BoardClass {
                @Test
                @DisplayName("null")
                void fail1() {
                    /* given */
                    board = null;

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        BoardImage boardImage = new BoardImage(image, board);
                    });

                    /* then */
                    assertEquals("존재하지 않는 게시물입니다.", exception.getMessage());
                }

                @Test
                @DisplayName("boardId가 null")
                void fail2() {
                    /* given */
                    Long boardId = null;

                    PostBoardRequestDto boardRequestDto = new PostBoardRequestDto("제목", "내용", null);
                    String profile =
                            "https://user-images.githubusercontent.com/76515226/140890775-30641b72-226a-4068-8a0a-9a306e8c68b4.png";
                    //게시물 작성자
                    User boardWriteUser = new User(1L, "aaa@aaa.com", "abab1234!",
                            "nao", profile, USER, "Y");

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        board = new Board(boardId, boardRequestDto, boardWriteUser);
                        BoardImage boardImage = new BoardImage(image, board);
                    });

                    /* then */
                    //Board 엔티티에서 발생한 exception
                    assertEquals("존재하지 않는 게시물입니다.", exception.getMessage());
                }

                @Test
                @DisplayName("boardId가 0")
                void fail3() {
                    /* given */
                    Long boardId = 0L;

                    PostBoardRequestDto boardRequestDto = new PostBoardRequestDto("제목", "내용", null);
                    String profile =
                            "https://user-images.githubusercontent.com/76515226/140890775-30641b72-226a-4068-8a0a-9a306e8c68b4.png";
                    //게시물 작성자
                    User boardWriteUser = new User(1L, "aaa@aaa.com", "abab1234!",
                            "nao", profile, USER, "Y");

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        board = new Board(boardId, boardRequestDto, boardWriteUser);
                        BoardImage boardImage = new BoardImage(image, board);
                    });

                    /* then */
                    //Board 엔티티에서 발생한 exception
                    assertEquals("존재하지 않는 게시물입니다.", exception.getMessage());
                }

                @Test
                @DisplayName("boardId가 음수")
                void fail4() {
                    /* given */
                    Long boardId = -1L;

                    PostBoardRequestDto boardRequestDto = new PostBoardRequestDto("제목", "내용", null);
                    String profile =
                            "https://user-images.githubusercontent.com/76515226/140890775-30641b72-226a-4068-8a0a-9a306e8c68b4.png";
                    //게시물 작성자
                    User boardWriteUser = new User(1L, "aaa@aaa.com", "abab1234!",
                            "nao", profile, USER, "Y");

                    /* when */
                    Exception exception = assertThrows(NullPointerException.class, () -> {
                        board = new Board(boardId, boardRequestDto, boardWriteUser);
                        BoardImage boardImage = new BoardImage(image, board);
                    });

                    /* then */
                    //Board 엔티티에서 발생한 exception
                    assertEquals("존재하지 않는 게시물입니다.", exception.getMessage());
                }
            }
        }
    }
}