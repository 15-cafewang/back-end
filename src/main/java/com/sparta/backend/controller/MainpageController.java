package com.sparta.backend.controller;

import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.recipes.RecipeListResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.recipe.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainpageController {
    private final RecipeService recipeService;

    //인기레시피 요청
    @GetMapping("/main/popular")
    public CustomResponseDto<?> getPopularRecipe(@RequestParam("sortBy") String sortBy, @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        List<RecipeListResponseDto> recipes = recipeService.getPopularRecipe(sortBy,userDetails.getUser());
        return new CustomResponseDto<>(1, "인기레시피 top3 조회완료" ,recipes);
    }

    @GetMapping("/main/recent")
    public CustomResponseDto<?> getRecentRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        List<RecipeListResponseDto> recipes = recipeService.getRecentRecipe(userDetails.getUser());
        return new CustomResponseDto<>(1, "최근레시피 top4 조회완료" ,recipes);
    }

    @GetMapping("/main/recommend")
    public CustomResponseDto<?> getRecommendedRecipe(@AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        RecipeListResponseDto recipes = recipeService.getRecommendedRecipe(userDetails.getUser());
        return new CustomResponseDto<>(1, "추천레시피 조회완료",recipes);
    }

    private void checkLogin(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new CustomErrorException("로그인된 유저만 사용가능한 기능입니다.");
        }
    }
}
