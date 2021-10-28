package com.sparta.backend.service;

import com.sparta.backend.domain.Recipe;
import com.sparta.backend.domain.Tag;
import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.repository.RecipesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecipesService {

    private final RecipesRepository recipesRepository;

    //todo: user정보도 넣어줘야 함
    public Recipe saveRecipe(PostRecipeRequestDto requestDto) {
        Recipe recipe = new Recipe(requestDto.getTitle(),requestDto.getContent(),"fakeURL");
        return recipesRepository.save(recipe);
    }

    public void deleteRecipe(Long recipeId) {
        Recipe recipe = recipesRepository.findById(recipeId).orElseThrow(()->
                new CustomErrorException("해당 아이디가 존재하지 않습니다")
        );
        recipesRepository.deleteById(recipeId);
        //todo:S3서버의 이미지도 지우기
    }
}
