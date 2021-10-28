package com.sparta.backend.controller;

import com.sparta.backend.dto.request.brand.PostBrandRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
public class BrandController {

    private final BrandService brandService;

    //브랜드 등록
    @PostMapping("/brands")
    public CustomResponseDto<String> createBrand(@RequestBody PostBrandRequestDto requestDto) { //todo: 매개변수 userDetails 추가

        CustomResponseDto<String> responseDto = null;
        Long brandId = brandService.createBrand(requestDto);    //todo: 매개변수 userDetails 추가

        if(brandId != null) {
            responseDto = new
                    CustomResponseDto<>(1, "브랜드 등록 완료", "");
        } else {
            responseDto = new
                    CustomResponseDto<>(-1, "브랜드 등록 실패", "");
        }

        return responseDto;
    }
}
