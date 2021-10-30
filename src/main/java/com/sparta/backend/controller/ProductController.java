package com.sparta.backend.controller;

import com.sparta.backend.domain.User;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.request.product.PostProductRequestDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ProductController {

    private final ProductService productService;

    //제품 등록
    @PostMapping("/admin/products")
    public CustomResponseDto<String>  createProduct(@RequestBody PostProductRequestDto requestDto) { //todo: 매개변수 userDetails 추가

        CustomResponseDto<String> responseDto = null;
        Long productId = productService.createProduct(requestDto); //todo: 매개변수 userDetails 추가

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
