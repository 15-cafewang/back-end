package com.sparta.backend.dto.response.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetReviewResponseDto {
    private Long reviewId;          //리뷰번호
    private String title;           //제목
    private String nickname;        //작성자
    private String content;         //리뷰내용
    private int star;            //별점
    private LocalDateTime regDate;  //작성시간
}
