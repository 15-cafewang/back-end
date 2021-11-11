package com.sparta.backend.validator;

import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;

import java.util.List;

public class PostRecipeRequestDtoValidator {
    public static void validateRecipeInput(PostRecipeRequestDto requestDto){
        if(requestDto.getTitle() == null || requestDto.getTitle().trim().equals("")){
            throw new IllegalArgumentException("제목은 필수입니다.");
        }

        if(requestDto.getTitle().length() > 100){
            throw new IllegalArgumentException("제목이 너무 깁니다.");
        }
    }

}
