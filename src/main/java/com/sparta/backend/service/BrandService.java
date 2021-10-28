package com.sparta.backend.service;

import com.sparta.backend.dto.request.brand.PostBrandRequestDto;
import com.sparta.backend.security.UserDetailsImpl;

public interface BrandService {

    //브랜드 등록
    public Long createBrand(PostBrandRequestDto requestDto); //todo: 매개변수 userDetails 추가
}
