package com.sparta.backend.validator;

import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;

import java.util.List;

public class PostRecipeRequestDtoValidator {
    public static void validateRecipeInput(PostRecipeRequestDto requestDto){
        if(requestDto.getTitle() == null){
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
    }

}
