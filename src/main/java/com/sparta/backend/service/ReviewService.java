package com.sparta.backend.service;

import com.sparta.backend.domain.Review;
import com.sparta.backend.dto.request.review.PostReviewRequestDto;
import com.sparta.backend.dto.request.review.PutReviewRequestDto;
import com.sparta.backend.dto.response.review.GetReviewResponseDto;
import com.sparta.backend.dto.response.review.PostReviewResponseDto;
import com.sparta.backend.security.UserDetailsImpl;

import java.util.List;

public interface ReviewService {

    //리뷰 작성
    public PostReviewResponseDto createReview(PostReviewRequestDto requestDto); //todo: 매개변수 UserDetailsImpl userDetails 추가

    //해당 제품에 대한 전체 리뷰 조회
    public List<GetReviewResponseDto> getReviews(Long productId);

    //해당 제품에 대한 상세 리뷰 조회
    public GetReviewResponseDto getDetailReview(Long reviewId);

    //리뷰 수정
    public Long updateReview(Long reviewId, PutReviewRequestDto requestDto, UserDetailsImpl userDetails);
}
