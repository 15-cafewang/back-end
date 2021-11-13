package com.sparta.backend.controller;

import com.sparta.backend.domain.recipe.Recipe;
import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.recipes.RecipeDetailResponsetDto;
import com.sparta.backend.dto.response.recipes.RecipeListResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.recipe.RecipeService;
import com.sparta.backend.service.recipe.TagService;
import com.sparta.backend.validator.PostRecipeRequestDtoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;
    private final TagService tagService;

    //레시피 등록
    @PostMapping("/recipes")
    public CustomResponseDto<?> postRecipe(PostRecipeRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        //todo:IOException처리
//        System.out.println("레시피 테스트:"+requestDto.getTitle()+"///"+ requestDto.getContent()+"///"+ requestDto.getImage());
//        System.out.println("테스트 title:"+requestDto.getTitle());
//        System.out.println("테스트 image:"+ Arrays.toString(requestDto.getImage()));
        checkLogin(userDetails);
        PostRecipeRequestDtoValidator.validateRecipeInput(requestDto);

//        레시피 먼저 생성, 등록
        Recipe savedRecipe = recipeService.saveRecipe(requestDto, userDetails.getUser());
//        태그 등록할때 저장한 레시피객체도 넣어줌
        System.out.println(requestDto.getTag());
        tagService.saveTags(requestDto.getTag(), savedRecipe);

        return new CustomResponseDto<>(1, "레시피 등록 성공", "");
    }

    //레시피 수정
    @PutMapping("/recipes/{recipeId}")
    public CustomResponseDto<?> updateRecipe(@PathVariable Long recipeId, PostRecipeRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        //todo:IOException처리
        checkLogin(userDetails);
        checkOwnership(recipeId, userDetails);

        //레시피 업데이트
        Recipe updatedRecipe = recipeService.updateRecipe(recipeId,requestDto);
        //태그 업데이트
        tagService.updateTags(requestDto.getTag(),updatedRecipe);

        return new CustomResponseDto<>(1, "레시피 수정 성공", "");
    }

    //레시피 삭제
    @DeleteMapping("recipes/{recipeId}")
    public CustomResponseDto<?> deleteRecipe(@PathVariable Long recipeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        checkLogin(userDetails);
        checkOwnership(recipeId, userDetails);

        recipeService.deleteRecipe(recipeId);
        return new CustomResponseDto<>(1, "레시피 삭제 성공", "");
    }

    //레시피 상세조회
    @GetMapping("recipes/{recipeId}")
    public CustomResponseDto<?> getRecipeDetail(@PathVariable Long recipeId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        RecipeDetailResponsetDto recipeDetailResponsetDto = recipeService.getRecipeDetail(recipeId, userDetails);
        return new CustomResponseDto<>(1, "레시피 조회 성공", recipeDetailResponsetDto);
    }

    //레시피 목록조회
    @GetMapping("recipes/list")
    public CustomResponseDto<?> getRecipes(@RequestParam("page") int page,
                                           @RequestParam("size") int size,
                                           @RequestParam("isAsc") boolean isAsc,
                                           @RequestParam("sortBy") String sortBy,
                                           @RequestParam("sortByLike") Boolean sortByLike,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        page = page-1;
        Page<RecipeListResponseDto> recipesByPage = recipeService.getRecipesByPage(page, size, isAsc, sortBy,sortByLike, userDetails);
        return new CustomResponseDto<>(1, "레시피 리스트 성공", recipesByPage);
    }

    //레시피 좋아요 등록/취소
    @GetMapping("recipes/likes/{postId}")
    public CustomResponseDto<?> likeRecipe(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        String resultMessage = recipeService.likeRecipe(postId, userDetails.getUser());
        return new CustomResponseDto<>(1, resultMessage,"");
    }

    //레시피 검색
    @GetMapping("/search/recipe")
    public CustomResponseDto<?> searchRecipe(@RequestParam("keyword") String keyword,
                                             @RequestParam("withTag") boolean withTag,
                                             @RequestParam("page") int page,
                                             @RequestParam("size") int size,
                                             @RequestParam("isAsc") boolean isAsc,
                                             @RequestParam("sortBy") String sortBy,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        //태그로 검색
        Page<RecipeListResponseDto> recipeByPage= recipeService.searchRecipe(withTag,keyword, page, size, isAsc, sortBy,userDetails);
        return new CustomResponseDto<>(1, "레시피 리스트 성공", recipeByPage);
    }

    private void checkLogin(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new CustomErrorException("로그인된 유저만 사용가능한 기능입니다.");
        }
    }

    private void checkOwnership(Long recipeId, UserDetailsImpl userDetails){
        Optional<Recipe> recipe = recipeService.findById(recipeId);
        if(recipe.isEmpty()) throw new NoSuchElementException("해당 게시물이 존재하지 않습니다");
        if(!recipe.get().getUser().getEmail().equals(userDetails.getUser().getEmail())) throw new CustomErrorException("본인의 게시물만 수정,삭제 가능합니다.");
    }
}
