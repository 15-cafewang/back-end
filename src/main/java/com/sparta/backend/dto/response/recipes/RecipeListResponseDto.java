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
    private String title;
    private String content;
    private String image;
    private LocalDateTime regdate;
    private int commentCount;
    private int likeCount;
    private boolean likeStatus;


    public RecipeListResponseDto(Recipe recipe, UserDetailsImpl userDetails, RecipeLikesRepository recipeLikesRepository){
        this.recipeId = recipe.getId();
        this.nickname = recipe.getUser().getNickname();
        this.title = recipe.getTitle();
        this.content = recipe.getContent();
        this.regdate = recipe.getRegDate();
        this.commentCount = recipe.getRecipeCommentList().size();
//        this.image = recipe.getImage();
        this.likeCount = recipe.getRecipeLikesList().size();

        Optional<RecipeLikes> foundRecipeLike = recipeLikesRepository.findByRecipeIdAndUserId(recipe.getId(),userDetails.getUser().getId());
        this.likeStatus = foundRecipeLike.isPresent();
    }
}