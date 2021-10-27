package com.sparta.backend.service;

import com.sparta.backend.domain.Recipe;
import com.sparta.backend.domain.Tag;
import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.repository.RecipesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecipesService {

    private final RecipesRepository recipesRepository;

    public Recipe saveRecipe(PostRecipeRequestDto requestDto) {
        Recipe recipe = new Recipe(requestDto.getTitle(),requestDto.getContent(),"fakeURL");
        return recipesRepository.save(recipe);
    }
}
