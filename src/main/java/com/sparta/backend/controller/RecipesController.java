package com.sparta.backend.controller;

import com.sparta.backend.domain.Recipe;
import com.sparta.backend.domain.Tag;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.service.RecipesService;
import com.sparta.backend.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RecipesController {
    private final RecipesService recipeService;
    private final TagService tagService;

    @PostMapping("/recipes")
    public CustomResponseDto<?> postRecipe(PostRecipeRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
        //todo: checkLogin
        //todo: imgae S3에 등록

        //레시피, 태그, 레시피태그 등록
        //레시피 먼저 생성, 등록
        Recipe savedRecipe = recipeService.saveRecipe(requestDto);
        //태그 등록할때 저장한 레시피객체도 넣어줌
        tagService.saveTags(requestDto.getTag(), savedRecipe);

        return new CustomResponseDto<>(1,"레시피 등록 성공","");
    }

//    @PutMapping("/recipes/{recipeId}")
//    public CustomResponseDto<?> updateRecipe(PostRecipeRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
//
//
//    }

    @DeleteMapping("recipes/{recipeId}")
    public CustomResponseDto<?> deleteRecipe(@PathVariable Long recipeId, @AuthenticationPrincipal UserDetails userDetails){
        //todo: checkLogin

        recipeService.deleteRecipe(recipeId);
        return new CustomResponseDto<>(1,"레시피 삭제 성공","");
    }




}
