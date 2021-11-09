package com.sparta.backend.dto.request.recipes;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostRecipeRequestDto {
    private String title;
    private String content;
    private int price;
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
