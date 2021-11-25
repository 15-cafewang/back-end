package com.sparta.backend.dto.request.cafe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class CafePutRequestDto {
    private String title;
    private String content;
    private String location;
    private List<String> tag;
    private MultipartFile[] image;
    private List<String> deleteImage;
}
