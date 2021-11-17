package com.sparta.backend.dto.request.board;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
public class PutBoardRequestDto {
    private String title;
    private String content;
    private List<String> deleteImage;
    private MultipartFile[] image;
}
