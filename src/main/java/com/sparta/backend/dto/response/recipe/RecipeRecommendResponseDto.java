package com.sparta.backend.dto.response.recipe;

import com.sparta.backend.domain.user.User;
import com.sparta.backend.domain.recipe.Recipe;
import com.sparta.backend.domain.recipe.RecipeLike;
import com.sparta.backend.repository.recipe.RecipeLikesRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class RecipeRecommendResponseDto {
    private Long recipeId;
    private String nickname;
    private String title;
    private String content;
    private List<String> images = new ArrayList<>();
    private LocalDateTime regDate;
    private int commentCount;
    private int likeCount;
    private String location;
    private boolean likeStatus;
    private String tagName;


    public RecipeRecommendResponseDto(Recipe recipe, UserDetailsImpl userDetails, RecipeLikesRepository recipeLikesRepository){
        this.recipeId = recipe.getId();
        this.nickname = recipe.getUser().getNickname();
        this.title = recipe.getTitle();
        this.content = recipe.getContent();
        this.regDate = recipe.getRegDate();
        this.commentCount = recipe.getRecipeCommentList().size();
        recipe.getRecipeImagesList().forEach((RecipeImage)->this.images.add(RecipeImage.getImage()));
        this.likeCount = recipe.getRecipeLikeList().size();
        this.location = recipe.getLocation();

        Optional<RecipeLike> foundRecipeLike = recipeLikesRepository.findByRecipeIdAndUserId(recipe.getId(),userDetails.getUser().getId());
        this.likeStatus = foundRecipeLike.isPresent();
    }
//
    public RecipeRecommendResponseDto(Recipe recipe, String tagName,User user, RecipeLikesRepository recipeLikesRepository){
        this.recipeId = recipe.getId();
        this.nickname = recipe.getUser().getNickname(); //todo:N+1 해결하면 될듯
        this.title = recipe.getTitle();
        this.content = recipe.getContent();
        this.regDate = recipe.getRegDate();
        this.commentCount = recipe.getRecipeCommentList().size();
        recipe.getRecipeImagesList().forEach((RecipeImage)->this.images.add(RecipeImage.getImage()));
        this.likeCount = recipe.getRecipeLikeList().size();
        this.location = recipe.getLocation();

        Optional<RecipeLike> foundRecipeLike = recipeLikesRepository.findByRecipeAndUser(recipe,user);
        this.likeStatus = foundRecipeLike.isPresent();
        this.tagName = tagName;
    }

    public RecipeRecommendResponseDto(Optional<Recipe> recipe, User user, RecipeLikesRepository likesRepository) {
        this.recipeId = recipe.get().getId();
        this.nickname = recipe.get().getUser().getNickname();
        this.title = recipe.get().getTitle();
        this.content = recipe.get().getContent();
        this.regDate = recipe.get().getRegDate();
        this.commentCount = recipe.get().getRecipeCommentList().size();
        recipe.get().getRecipeImagesList().forEach((RecipeImage)->this.images.add(RecipeImage.getImage()));
        this.likeCount = recipe.get().getRecipeLikeList().size();
        this.location = recipe.get().getLocation();

        Optional<RecipeLike> foundRecipeLike = likesRepository.findByRecipeIdAndUserId(recipe.get().getId(), user.getId());
        this.likeStatus = foundRecipeLike.isPresent();
    }
}