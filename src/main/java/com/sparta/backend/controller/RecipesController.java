package com.sparta.backend.controller;

import com.sparta.backend.domain.Recipe.Recipe;
import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.recipes.RecipeDetailResponsetDto;
import com.sparta.backend.dto.response.recipes.RecipeListResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.RecipesService;
import com.sparta.backend.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RecipesController {
    private final RecipesService recipeService;
    private final TagService tagService;

    //레시피 등록
    @PostMapping("/recipes")
    public CustomResponseDto<?> postRecipe(PostRecipeRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        //todo:IOException처리
        //todo: checkLogin
        //레시피 먼저 생성, 등록
        Recipe savedRecipe = recipeService.saveRecipe(requestDto, userDetails);
        //태그 등록할때 저장한 레시피객체도 넣어줌
        tagService.saveTags(requestDto.getTag(), savedRecipe);

        return new CustomResponseDto<>(1, "레시피 등록 성공", "");
    }

    //레시피 수정
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

    //레시피 삭제
    @DeleteMapping("recipes/{recipeId}")
    public CustomResponseDto<?> deleteRecipe(@PathVariable Long recipeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        //todo: checkLogin
        //todo: 글쓴사람이 로그인한사람인지 체크
        recipeService.deleteRecipe(recipeId);
        return new CustomResponseDto<>(1, "레시피 삭제 성공", "");
    }

    //레시피 상세조회
    @GetMapping("recipes/{recipeId}")
    public CustomResponseDto<?> getRecipeDetail(@PathVariable Long recipeId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        //todo: checkLogin
        RecipeDetailResponsetDto recipeDetailResponsetDto = recipeService.getRecipeDetail(recipeId, userDetails);
        return new CustomResponseDto<>(1, "레시피 조회 성공", recipeDetailResponsetDto);
    }

    //레시피 목록조회
    @GetMapping("recipes/list")
    public CustomResponseDto<?> getRecipes(@RequestParam("page") int page,
                                           @RequestParam("size") int size,
                                           @RequestParam("isAsc") boolean isAsc,
                                           @RequestParam("sortBy") String sortBy,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails){
        //todo: checkLogin
        page = page-1;
        Page<RecipeListResponseDto> recipesByPage = recipeService.getRecipesByPage(page, size, isAsc, sortBy, userDetails);
        return new CustomResponseDto<>(1, "레시피 리스트 성공", recipesByPage);
    }

    //레시피 좋아요 등록/취소
    @GetMapping("recipes/likes/{postId}")
    public CustomResponseDto<?> likeRecipe(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        //todo: checkLogin
        String resultMessage = recipeService.likeRecipe(postId, userDetails);
        return new CustomResponseDto<>(1, resultMessage,"");
    }
}
