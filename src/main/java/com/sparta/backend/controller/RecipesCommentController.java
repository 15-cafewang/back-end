package com.sparta.backend.controller;

import com.sparta.backend.dto.request.recipes.PostCommentRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.RecipeCommentService;
import com.sparta.backend.service.RecipesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecipesCommentController {

    private final RecipeCommentService recipeCommentService;

    @PostMapping("/recipes/comment")
    public CustomResponseDto<?> postComment(@RequestBody PostCommentRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        //todo: checkLogin
        recipeCommentService.saveComment(requestDto,userDetails);
        return new CustomResponseDto<>(1, "댓글 등록 성공!", "");
    }
}
