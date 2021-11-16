package com.sparta.backend.dto.request.recipes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public class PutRecipeRequestDto {
    private String title;
    private String content;
    private Integer price;
    private List<String> tag;
    private MultipartFile[] image;
    private List<String> deleteImage;
}
