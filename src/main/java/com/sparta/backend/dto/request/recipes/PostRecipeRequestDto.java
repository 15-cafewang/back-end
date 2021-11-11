package com.sparta.backend.dto.request.recipes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostRecipeRequestDto {
    private String title;
    private String content;
    private Integer price;
    private List<String> tag;
    private MultipartFile[] image;

}
