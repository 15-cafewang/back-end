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
    }
}