package com.sparta.backend.dto.request.board;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class PostBoardRequestDto {
    private String title;
    private String content;
    private MultipartFile image;
}
