package com.sparta.backend.controller;

import com.sparta.backend.domain.Comment;
import com.sparta.backend.dto.request.recipes.PostCommentRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.recipes.RecipeCommentResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.RecipeCommentService;
import com.sparta.backend.service.RecipesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecipesCommentController {

    private final RecipeCommentService commentService;

    @PostMapping("/recipes/comment")
    public CustomResponseDto<?> postComment(@RequestBody PostCommentRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        //todo: checkLogin
        commentService.saveComment(requestDto,userDetails);
        return new CustomResponseDto<>(1, "댓글 등록 성공!", "");
    }

    @GetMapping("/recipes/comment/{recipeId}")
    public CustomResponseDto<?> getComment(@PathVariable Long recipeId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        List<RecipeCommentResponseDto> responseDtoList = commentService.getComment(recipeId, userDetails);
        return new CustomResponseDto<>(1,"댓글 조회 성공!",responseDtoList);
    }
}
