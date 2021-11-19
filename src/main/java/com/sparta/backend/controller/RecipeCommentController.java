package com.sparta.backend.controller;

import com.sparta.backend.domain.recipe.RecipeComment;
import com.sparta.backend.dto.request.recipes.PostCommentRequestDto;
import com.sparta.backend.dto.request.recipes.RecipeCommentUpdateRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.recipes.RecipeCommentResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.recipe.RecipeCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class RecipeCommentController {

    private final RecipeCommentService commentService;

    //댓글 입력
    @PostMapping("/recipes/comment")
    public CustomResponseDto<?> postComment(@RequestBody PostCommentRequestDto requestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        RecipeCommentResponseDto responseDto = commentService.saveComment(requestDto,userDetails);
        return new CustomResponseDto<>(1, "댓글 등록 성공!", responseDto);
    }

    //댓글들 조회
//    @GetMapping("/recipes/comment/{recipeId}")
//    public CustomResponseDto<?> getComment(@PathVariable Long recipeId,
//                                           @AuthenticationPrincipal UserDetailsImpl userDetails){
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
        checkLogin(userDetails);
        page = page-1;
        Page<RecipeCommentResponseDto> responseDtoList = commentService.getCommentByPage(recipeId, page, size, isAsc, userDetails);
        return new CustomResponseDto<>(1,"댓글 조회 성공!",responseDtoList);
    }

    //댓글 삭제
    @DeleteMapping("/recipes/comment/{commentId}")
    public CustomResponseDto<?> deleteComment(@PathVariable Long commentId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        checkOwnership(commentId,userDetails);
        commentService.deleteComment(commentId, userDetails);
        return new CustomResponseDto<>(1,"댓글 삭제 성공!", "");
    }

    //댓글 수정
    @PutMapping("/recipes/comment/{commentId}")
    public CustomResponseDto<?> updateComment(@PathVariable Long commentId,
                                              @RequestBody RecipeCommentUpdateRequestDto updateRequestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        checkOwnership(commentId,userDetails);
        commentService.updateComment(commentId, updateRequestDto, userDetails);
        return new CustomResponseDto<>(1,"댓글 수정 성공","");
   }

   //댓글 좋아요 등록/취소
    @GetMapping("/recipes/comment/likes/{commentId}")
    public CustomResponseDto<?> likeComment(@PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        String resultMessage = commentService.likeComment(commentId, userDetails.getUser());
        return new CustomResponseDto<>(1,resultMessage, "");
    }


    private void checkLogin(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new CustomErrorException("로그인된 유저만 사용가능한 기능입니다.");
        }
    }
    private void checkOwnership(Long commentId, UserDetailsImpl userDetails){
        Optional<RecipeComment> recipe = commentService.findById(commentId);
        if(!recipe.get().getUser().getEmail().equals(userDetails.getUser().getEmail())) throw new CustomErrorException("본인의 게시물만 수정,삭제 가능합니다.");
    }
}
