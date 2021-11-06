package com.sparta.backend.controller;

import com.sparta.backend.domain.Recipe.Recipe;
import com.sparta.backend.dto.queryInterface.PopularRecipeInterface;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.Recipe.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainpageController {
    private final RecipeService recipeService;

    //인기레시피 요청
    @GetMapping("/main/popular")
    public CustomResponseDto<?> getPopularRecipe(@RequestParam("sortBy") String sortBy, @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        List<Long> recipes = recipeService.getPopularRecipe(sortBy,userDetails.getUser());
        return new CustomResponseDto<>(1, "dz" ,recipes);
    }

    private void checkLogin(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new CustomErrorException("로그인된 유저만 사용가능한 기능입니다.");
        }
    }
}
