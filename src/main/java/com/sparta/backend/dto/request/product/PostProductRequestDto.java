package com.sparta.backend.dto.request.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostProductRequestDto {
    private String title;       //제품명
    private String content;     //제품 설명
    private String image;       //사진
    private Long brandId;       //브랜드번호
}
