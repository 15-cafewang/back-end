package com.sparta.backend.controller;

import com.sparta.backend.domain.Recipe;
import com.sparta.backend.domain.Tag;
import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.service.RecipesService;
import com.sparta.backend.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RecipesController {
    private final RecipesService recipeService;
    private final TagService tagService;

    @PostMapping("/recipes")
    public CustomResponseDto postRecipe(PostRecipeRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
        //todo: checkLogin
        //todo: imgae S3에 등록

        List<Tag> tag = recipeService.saveTags(requestDto.getTag());
        Recipe recipe = recipeService.saveRecipe(requestDto);

        for

        return new CustomResponseDto(1,"레시피 등록 성공","");
    }
}
