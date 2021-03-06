package com.sparta.backend.dto.request.board;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PostBoardRequestDto {
    private String title;
    private String content;
    private MultipartFile[] image;
}
