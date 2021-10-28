package com.sparta.backend.service;

import com.sparta.backend.domain.Brand;
import com.sparta.backend.dto.request.brand.PostBrandRequestDto;
import com.sparta.backend.repository.BrandRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BrandServiceImpl implements BrandService{

    private final BrandRepository brandRepository;

    //브랜드 등록
    public Long createBrand(PostBrandRequestDto requestDto, UserDetailsImpl userDetails) {
        Brand brand = null;
        if(userDetails != null) {   //로그인 되어있을 때
            brand = new Brand(requestDto);
            brandRepository.save(brand);
        } else {
            throw new NullPointerException();
        }

        return brand.getId();
    }

}
