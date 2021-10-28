package com.sparta.backend.controller;

import com.sparta.backend.domain.User;
import com.sparta.backend.dto.request.brand.PostBrandRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
public class BrandController {

    private final BrandService brandService;

    //브랜드 등록
    @PostMapping("/brands")
    public CustomResponseDto<String> createBrand(@RequestBody PostBrandRequestDto requestDto) {
        //로그인 가정
        User user = new User("aaa@aaa.com", "abab1234!", "aaa");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        CustomResponseDto<String> responseDto = null;
        Long brandId = brandService.createBrand(requestDto, userDetails);

        if(brandId != null) {
            responseDto = new
                    CustomResponseDto(1, "브랜드 등록 완료", "");
        } else {
            responseDto = new
                    CustomResponseDto(-1, "브랜드 등록 실패", "");
        }

        return responseDto;
    }
}
