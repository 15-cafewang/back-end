package com.sparta.backend.service;

import com.sparta.backend.dto.request.product.PostProductRequestDto;
import com.sparta.backend.security.UserDetailsImpl;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {
    //제품 등록
    public Long createProduct(PostProductRequestDto requestDto, UserDetailsImpl userDetails);
}
