package com.sparta.backend.dto.request.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostBoardRequestDto {
    private String title;
    private String content;
    private MultipartFile image;
}
