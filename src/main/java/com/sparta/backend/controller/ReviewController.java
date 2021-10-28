package com.sparta.backend.controller;

import com.sparta.backend.domain.User;
import com.sparta.backend.dto.request.review.PostReviewRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.review.PostReviewResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    //리뷰 작성
    @PostMapping("/products/reviews")
    public CustomResponseDto<?> createReview(@RequestParam PostReviewRequestDto requestDto,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {

        PostReviewResponseDto postReviewResponseDto = reviewService.createReview(requestDto, userDetails);

        if(postReviewResponseDto != null) {
            return new CustomResponseDto<>(1, "리뷰 작성 완료", postReviewResponseDto);
        } else {
            return new CustomResponseDto<>(-1, "리뷰 작성 실패", "");
        }
    }

}
