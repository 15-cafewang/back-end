package com.sparta.backend.controller;

import com.sparta.backend.domain.Recipe;
import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.recipes.RecipeDetailResponsetDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.RecipesService;
import com.sparta.backend.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RecipesController {
    private final RecipesService recipeService;
    private final TagService tagService;

    @PostMapping("/recipes")
    public CustomResponseDto<?> postRecipe(PostRecipeRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        //todo:IOException처리
        //todo: checkLogin
        //레시피 먼저 생성, 등록
        Recipe savedRecipe = recipeService.saveRecipe(requestDto);
        //태그 등록할때 저장한 레시피객체도 넣어줌
        tagService.saveTags(requestDto.getTag(), savedRecipe);

        return new CustomResponseDto<>(1, "레시피 등록 성공", "");
    }

    @PutMapping("/recipes/{recipeId}")
    public CustomResponseDto<?> updateRecipe(@PathVariable Long recipeId, PostRecipeRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        //todo:IOException처리
        //todo: checkLogin

        //레시피 업데이트
        Recipe updatedRecipe = recipeService.updateRecipe(recipeId,requestDto,userDetails);
        //태그 업데이트
        //todo: 태그가 업데이트 되지 않았다면 걍 패스하는 코드 추가
        tagService.updateTags(requestDto.getTag(),updatedRecipe);

        return new CustomResponseDto<>(1, "레시피 수정 성공", "");
    }

    @DeleteMapping("recipes/{recipeId}")
    public CustomResponseDto<?> deleteRecipe(@PathVariable Long recipeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        //todo: checkLogin
        //todo: 글쓴사람이 로그인한사람인지 체크
        recipeService.deleteRecipe(recipeId);
        return new CustomResponseDto<>(1, "레시피 삭제 성공", "");
    }

    @GetMapping("recipes/{recipeId}")
    public CustomResponseDto<?> getRecipeDetail(@PathVariable Long recipeId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        RecipeDetailResponsetDto recipeDetailResponsetDto = recipeService.getRecipeDetail(recipeId, userDetails);
        return new CustomResponseDto<>(1, "마이페이지 조회 성공", recipeDetailResponsetDto);
    }


}
