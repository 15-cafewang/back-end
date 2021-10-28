package com.sparta.backend.service;

import com.sparta.backend.domain.Product;
import com.sparta.backend.domain.Review;
import com.sparta.backend.domain.User;
import com.sparta.backend.repository.ProductRepository;
import com.sparta.backend.repository.ReviewRepository;
import com.sparta.backend.dto.request.review.PostReviewRequestDto;
import com.sparta.backend.dto.response.review.PostReviewResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    //리뷰 작성
    @Transactional
    public PostReviewResponseDto createReview(PostReviewRequestDto requestDto, UserDetailsImpl userDetails) {
        //로그인한 사용자 정보
        User user = userDetails.getUser();

        Long productId = requestDto.getProductId();     //제품 아이디

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NullPointerException("찾는 제품이 없습니다.")
        );

        Review review = new Review(requestDto, product, user);
        reviewRepository.save(review);

        String nickname = user.getNickname();           //로그인한 사용자 닉네임
        String content = requestDto.getContent();       //리뷰 내용
        String image = requestDto.getImage();           //사진
        LocalDateTime regDate = review.getRegDate();    //작성 시간

        PostReviewResponseDto responseDto =
                new PostReviewResponseDto(productId, nickname, content, image, regDate);

        return responseDto;
    }

}
