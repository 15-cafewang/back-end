package com.sparta.backend.controller;

import com.sparta.backend.domain.Comment;
import com.sparta.backend.dto.request.recipes.PostCommentRequestDto;
import com.sparta.backend.dto.request.recipes.RecipeCommentUpdateRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.recipes.RecipeCommentResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.RecipeCommentService;
import com.sparta.backend.service.RecipesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecipesCommentController {

    private final RecipeCommentService commentService;

    //댓글 입력
    @PostMapping("/recipes/comment")
    public CustomResponseDto<?> postComment(@RequestBody PostCommentRequestDto requestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails){
        //todo: checkLogin
        commentService.saveComment(requestDto,userDetails);
        return new CustomResponseDto<>(1, "댓글 등록 성공!", "");
    }

    //댓글들 조회
//    @GetMapping("/recipes/comment/{recipeId}")
//    public CustomResponseDto<?> getComment(@PathVariable Long recipeId,
//                                           @AuthenticationPrincipal UserDetailsImpl userDetails){
//        //todo:checkLogin
//        List<RecipeCommentResponseDto> responseDtoList = commentService.getComment(recipeId, userDetails);
//        return new CustomResponseDto<>(1,"댓글 조회 성공!",responseDtoList);
//    }

    //댓글들 페이지로 조회
    @GetMapping("/recipes/comment/{recipeId}")
    public CustomResponseDto<?> getComment(@PathVariable Long recipeId,
                                           @RequestParam("page") int page,
                                           @RequestParam("size") int size,
                                           @RequestParam("isAsc") boolean isAsc,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails){
        //todo:checkLogin
        page = page-1;
        Page<RecipeCommentResponseDto> responseDtoList = commentService.getCommentByPage(recipeId, page, size, isAsc, userDetails);
        return new CustomResponseDto<>(1,"댓글 조회 성공!",responseDtoList);
    }

    //댓글 삭제
    @DeleteMapping("/recipes/comment/{commentId}")
    public CustomResponseDto<?> deleteComment(@PathVariable Long commentId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        //todo:checkLogin
        commentService.deleteComment(commentId, userDetails);
        return new CustomResponseDto<>(1,"댓글 삭제 성공!", "");
    }

    //댓글 수정
    @PutMapping("/recipes/comment/{commentId}")
    public CustomResponseDto<?> updateComment(@PathVariable Long commentId,
                                              @RequestBody RecipeCommentUpdateRequestDto updateRequestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        //todo:checkLogin
        commentService.updateComment(commentId, updateRequestDto, userDetails);
        return new CustomResponseDto<>(1,"댓글 수정 성공","");
   }
}
