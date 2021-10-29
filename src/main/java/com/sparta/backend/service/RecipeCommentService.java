package com.sparta.backend.service;

import com.sparta.backend.domain.Comment;
import com.sparta.backend.domain.Recipe;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.request.recipes.PostCommentRequestDto;
import com.sparta.backend.dto.request.recipes.RecipeCommentUpdateRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.recipes.RecipeCommentResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.repository.RecipeCommentRepository;
import com.sparta.backend.repository.RecipesRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RecipeCommentService {
    private final RecipeCommentRepository commentRepository;
    private final RecipesRepository recipesRepository;

    public void saveComment(PostCommentRequestDto requestDto, UserDetailsImpl userDetails) {

        Recipe recipe = recipesRepository.findById(requestDto.getRecipeId()).orElseThrow(()->
                new CustomErrorException("해당 댓글의 레시피가 존재하지 않습니다."));
        //todo: User도 넣어줘야 함.
        Comment comment = new Comment(requestDto.getContent(),null, recipe);
        commentRepository.save(comment);
    }

    public List<RecipeCommentResponseDto> getComment(Long recipeId, UserDetailsImpl userDetails){
        List<Comment> commentList = commentRepository.findAllByRecipeIdOrderByRegDateDesc(recipeId);
        List<RecipeCommentResponseDto> responseDtoList = new ArrayList<>();

        //todo: User완성되면 진짜 nickname 넣어주기
        commentList.forEach(comment ->
                responseDtoList.add(new RecipeCommentResponseDto(
                        comment.getId(),
//                        comment.getUser().getNickname(),
                        "mock nickname",
                        comment.getContent(),
                        comment.getRegDate()
                        )));
        return responseDtoList;
    }

    //댓글 삭제
    public void deleteComment(Long commentId, UserDetailsImpl userDetails) {
        commentRepository.deleteById(commentId);
    }

    //댓글 수정
    public void updateComment(Long commentId, RecipeCommentUpdateRequestDto updateRequestDto, UserDetailsImpl userDetails) {
        //todo: 해당 댓글이 로그인 한 사람의 댓글인지 확인

        Comment comment = commentRepository.findById(commentId).orElseThrow(()->
                new CustomErrorException("해당 댓글이 존재하지 않습니다"));
        comment.updateComment(updateRequestDto.getContent());
    }
}
