package com.sparta.backend.service;

import com.sparta.backend.domain.Brand;
import com.sparta.backend.domain.Product;
import com.sparta.backend.repository.BrandRepository;
import com.sparta.backend.repository.ProductRepository;
import com.sparta.backend.dto.request.product.PostProductRequestDto;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    //제품 등록
    @Transactional
    public Long createProduct(PostProductRequestDto requestDto) { //todo: 매개변수 userDetails 추가
        Product product = null;
//        if(userDetails != null) {   //로그인했을 경우 //todo: 매개변수 userDetails 추가시 주석 해제
            Long brandId = requestDto.getBrandId(); //브랜드 아이디

            //DB에 Insert 되어있는 브랜드 이름 가져오기
            Brand brand = brandRepository.findById(brandId).orElseThrow(
                    () -> new NullPointerException("찾는 브랜드가 없습니다.")
            );

            //Insert
            product = new Product(requestDto, brand);
            productRepository.save(product);

//        } else {    //로그인 되어있지 않은 경우
//            throw new NullPointerException("로그인이 필요합니다."); //todo: 매개변수 userDetails 추가시 주석 해제
//        }

        //DB에 Insert한 제품 번호 리턴
        return product.getId();
    }
}
