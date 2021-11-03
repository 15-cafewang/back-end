package com.sparta.backend.dto.response.recipes;

import com.sparta.backend.domain.Recipe.Recipe;
import com.sparta.backend.domain.Recipe.RecipeLikes;
import com.sparta.backend.repository.RecipeLikesRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class RecipeListResponseDto {
    private Long recipeId;
    private String nickname;
    private String content;
    private LocalDateTime regdate;
    private int likeCount;
    private boolean likeStatus;

    public RecipeListResponseDto(Recipe recipe, UserDetailsImpl userDetails, RecipeLikesRepository recipeLikesRepository){
        this.recipeId = recipe.getId();
        //todo: user이름으로 해야 함
        this.nickname = recipe.getUser().getNickname();
        this.content = recipe.getTitle();
        this.regdate = recipe.getRegDate();
        this.likeCount = recipe.getRecipeLikesList().size();
        
        Optional<RecipeLikes> foundRecipeLike = recipeLikesRepository.findByRecipeIdAndUserId(recipe.getId(),userDetails.getUser().getId());
        this.likeStatus = foundRecipeLike.isPresent();
    }
}