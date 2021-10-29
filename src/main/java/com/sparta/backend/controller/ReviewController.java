package com.sparta.backend.controller;

import com.sparta.backend.dto.response.review.GetReviewResponseDto;
import com.sparta.backend.dto.request.review.PostReviewRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.review.PostReviewResponseDto;
import com.sparta.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    //리뷰 작성
    @PostMapping("/products/reviews")
    public CustomResponseDto<?> createReview(@RequestBody PostReviewRequestDto requestDto) { //todo: 매개변수 UserDetailsImpl userDetails 추가

        PostReviewResponseDto postReviewResponseDto = reviewService.createReview(requestDto); //todo: 매개변수 userDetails 추가

        if(postReviewResponseDto != null) {
            return new CustomResponseDto<>(1, "리뷰 작성 완료", postReviewResponseDto);
        } else {
            return new CustomResponseDto<>(-1, "리뷰 작성 실패", "");
        }
    }

    //리뷰 조회
    @GetMapping("/products/reviews/{productId}")
    public CustomResponseDto<?> getReviews(@PathVariable Long productId) {
        List<GetReviewResponseDto> reviewList = reviewService.getReviews(productId);

        if(reviewList != null) {
            return new CustomResponseDto<>(1, "리뷰 조회 성공", reviewList);
        } else {
            return new CustomResponseDto<>(-1, "리뷰 조회 실패", "");
        }
    }

}
