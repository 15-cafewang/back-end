package com.sparta.backend.dto.request.recipes;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostRecipeRequestDto {
    private String title;
    private String content;
    private MultipartFile image;
    private int price;
    private List<String> tag;
}
