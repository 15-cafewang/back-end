package com.sparta.backend.controller;

import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.service.RecipesService;
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

    @PostMapping("/recipes")
    public CustomResponseDto postRecipe(PostRecipeRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
        //todo: checkLogin
//        String imageUrl =
        List<String> tags = requestDto.getTag();
        log.info("tag = {}",tags);
        return new CustomResponseDto(1,"레시피 등록 성공","");
    }
}
