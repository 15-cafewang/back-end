package com.sparta.backend.dto.response.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostReviewResponseDto {
    private Long productId;         //제품번호
    private String nickname;        //작성자
    private String title;           //제목
    private String content;         //리뷰 내용
    private String image;           //사진
    private int star;               //별점
    private LocalDateTime regDate;  //작성시간
}
