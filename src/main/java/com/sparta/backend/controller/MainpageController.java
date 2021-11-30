package com.sparta.backend.controller;

import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.cafe.CafeListResponseDto;
import com.sparta.backend.dto.response.cafe.CafeRecommendResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.cafe.CafeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainpageController {
    private final CafeService cafeService;

    //인기카페 요청
    @GetMapping("/main/popular")
    public ResponseEntity<?> getPopularCafe(@RequestParam("sortBy") String sortBy, @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        List<CafeListResponseDto> cafes = cafeService.getPopularCafe(sortBy,userDetails.getUser());
        return new ResponseEntity<>(new CustomResponseDto<>(1, "인기카페 top3 조회완료" ,cafes),HttpStatus.OK);
    }

    @GetMapping("/main/recent")
    public ResponseEntity<?> getRecentCafe(@AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        List<CafeListResponseDto> cafes = cafeService.getRecentCafe(userDetails.getUser());
        return new ResponseEntity<>(new CustomResponseDto<>(1, "최근카페 top3 조회완료" ,cafes),HttpStatus.OK);
    }

    @GetMapping("/main/recommend")
    public ResponseEntity<?> getRecommendedCafe(@AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        List<CafeRecommendResponseDto> cafes = cafeService.getRecommendedCafe(userDetails.getUser());
        return new ResponseEntity<>(new CustomResponseDto<>(1, "추천카페 조회완료",cafes),HttpStatus.OK);
    }

    private void checkLogin(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new CustomErrorException("로그인된 유저만 사용가능한 기능입니다.");
        }
    }
}
