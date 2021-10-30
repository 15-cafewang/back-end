package com.sparta.backend.dto.request.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PutReviewRequestDto {
    private String title;       //제목
    private String content;     //리뷰내용
    private String image;       //이미지
    private int star;           //별점
}
