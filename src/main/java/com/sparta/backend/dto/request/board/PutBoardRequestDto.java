package com.sparta.backend.dto.request.board;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class PutBoardRequestDto {
    private String title;
    private String content;
    private MultipartFile[] image;
}
