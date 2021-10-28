package com.sparta.backend.controller;

import com.sparta.backend.domain.User;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.request.product.PostProductRequestDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class ProductController {

    private final ProductService productService;

    //제품 등록
    @PostMapping("/products")
    public CustomResponseDto<String>  createProduct(@RequestParam PostProductRequestDto requestDto) {
        //로그인 가정
        User user = new User("aaa@aaa.com", "abab1234!", "aaa");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        CustomResponseDto<String> responseDto = null;
        Long productId = productService.createProduct(requestDto, userDetails);

        if(productId != null) {
            responseDto =
                    new CustomResponseDto<>(1, "제품 등록 완료", "");
        } else {
            responseDto =
                    new CustomResponseDto<>(-1, "제품 등록 실패", "");
        }

        return responseDto;
    }
}
