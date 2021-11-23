package com.sparta.backend.validator;

import com.sparta.backend.dto.request.recipe.PostRecipeRequestDto;

public class PostRecipeRequestDtoValidator {
    public static void validateRecipeInput(PostRecipeRequestDto requestDto){
        if(requestDto.getTitle() == null || requestDto.getTitle().trim().equals("")){
            throw new IllegalArgumentException("제목은 필수입니다.");
        }

        if(requestDto.getTitle().length() > 100){
            throw new IllegalArgumentException("제목이 너무 깁니다.");
        }

        if(requestDto.getContent().length()>1000){
            throw new IllegalArgumentException("내용의 길이가 1000자를 초과하였습니다");
        }

        if( requestDto.getImage().length >5){
            throw new IllegalArgumentException("사진은 5장을 초과할 수 없습니다.");
        }

        if( requestDto.getImage()[0].getSize() == 0){
            throw new IllegalArgumentException("카페등록시 사진은 필수입니다.");
        }

        if(requestDto.getTag().size() >10){
            throw new IllegalArgumentException("태그가 너무 많습니다.");
        }

        requestDto.getTag().forEach(tag->{
            if(tag.length()>50) throw new IllegalArgumentException("태그가 너무 깁니다.");
        });

        if( requestDto.getLocation().length() > 100){
            throw new IllegalArgumentException("장소의 길이가 100자를 초과하였습니다.");
        }

    }

}
