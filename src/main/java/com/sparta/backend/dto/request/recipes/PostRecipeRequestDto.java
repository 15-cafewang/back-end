package com.sparta.backend.dto.request.recipes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
public class PostRecipeRequestDto {
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;
    private String content;
    private Integer price;
    private List<String> tag;
    private MultipartFile[] image;

//    public PostRecipeRequestDto(String title, String content, int price, List<String> tag, MultipartFile image1, MultipartFile image2, MultipartFile image3, MultipartFile image4, MultipartFile image5) {
//        this.title = title;
//        this.content = content;
//        this.price = price;
//        this.tag = tag;
//        this.image1 = image1;
//        this.image2 = image2;
//        this.image3 = image3;
//        this.image4 = image4;
//        this.image5 = image5;
//    }
}
