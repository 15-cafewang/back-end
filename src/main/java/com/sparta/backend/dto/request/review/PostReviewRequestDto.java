package com.sparta.backend.dto.request.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostReviewRequestDto {
    private Long productId;     //제품 아이디
    private String title;       //제목
    private String content;     //리뷰 내용
    private String image;       //사진
    private int star;        //별점
}
