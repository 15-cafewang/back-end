package com.sparta.backend.dto.request.brand;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostBrandRequestDto {
    private String name;    //브랜드명
    private String image;   //브랜드 이미지
}
