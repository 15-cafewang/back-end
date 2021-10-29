package com.sparta.backend.service;

import com.sparta.backend.dto.request.review.PostReviewRequestDto;
import com.sparta.backend.dto.response.review.GetReviewResponseDto;
import com.sparta.backend.dto.response.review.PostReviewResponseDto;
import com.sparta.backend.security.UserDetailsImpl;

import java.util.List;

public interface ReviewService {

    //리뷰 작성
    public PostReviewResponseDto createReview(PostReviewRequestDto requestDto); //todo: 매개변수 UserDetailsImpl userDetails 추가

    //리뷰 조회
    List<GetReviewResponseDto> getReviews(Long productId);
}
