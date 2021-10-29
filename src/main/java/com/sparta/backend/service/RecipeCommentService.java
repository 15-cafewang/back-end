package com.sparta.backend.service;

import com.sparta.backend.domain.Comment;
import com.sparta.backend.domain.Recipe;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.request.recipes.PostCommentRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.repository.RecipeCommentRepository;
import com.sparta.backend.repository.RecipesRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
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
}
