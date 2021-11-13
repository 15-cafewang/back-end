package com.sparta.backend.dto.response.recipes;

import com.sparta.backend.domain.recipe.Recipe;
import com.sparta.backend.domain.recipe.RecipeLikes;
import com.sparta.backend.domain.User;
import com.sparta.backend.repository.RecipeLikesRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class RecipeListResponseDto {
    private Long recipeId;
    private String nickname;
    private String title;
    private String content;
    private List<String> images = new ArrayList<>();
    private LocalDateTime regDate;
    private int commentCount;
    private int likeCount;
    private Integer price;
    private boolean likeStatus;


    public RecipeListResponseDto(Recipe recipe, UserDetailsImpl userDetails, RecipeLikesRepository recipeLikesRepository){
        this.recipeId = recipe.getId();
        this.nickname = recipe.getUser().getNickname();
        this.title = recipe.getTitle();
        this.content = recipe.getContent();
        this.regDate = recipe.getRegDate();
        this.commentCount = recipe.getRecipeCommentList().size();
        recipe.getRecipeImagesList().forEach((RecipeImage)->this.images.add(RecipeImage.getImage()));
        this.likeCount = recipe.getRecipeLikesList().size();
        this.price = recipe.getPrice();

        Optional<RecipeLikes> foundRecipeLike = recipeLikesRepository.findByRecipeIdAndUserId(recipe.getId(),userDetails.getUser().getId());
        this.likeStatus = foundRecipeLike.isPresent();
    }
//
    public RecipeListResponseDto(Recipe recipe, User user, RecipeLikesRepository recipeLikesRepository){
        this.recipeId = recipe.getId();
        this.nickname = recipe.getUser().getNickname(); //todo:N+1 해결하면 될듯
        this.title = recipe.getTitle();
        this.content = recipe.getContent();
        this.regDate = recipe.getRegDate();
        this.commentCount = recipe.getRecipeCommentList().size();
        recipe.getRecipeImagesList().forEach((RecipeImage)->this.images.add(RecipeImage.getImage()));
        this.likeCount = recipe.getRecipeLikesList().size();
        this.price = recipe.getPrice();

        Optional<RecipeLikes> foundRecipeLike = recipeLikesRepository.findByRecipeAndUser(recipe,user);
        this.likeStatus = foundRecipeLike.isPresent();
    }

    public RecipeListResponseDto(Optional<Recipe> recipe, User user, RecipeLikesRepository likesRepository) {
        this.recipeId = recipe.get().getId();
        this.nickname = recipe.get().getUser().getNickname();
        this.title = recipe.get().getTitle();
        this.content = recipe.get().getContent();
        this.regDate = recipe.get().getRegDate();
        this.commentCount = recipe.get().getRecipeCommentList().size();
        recipe.get().getRecipeImagesList().forEach((RecipeImage)->this.images.add(RecipeImage.getImage()));
        this.likeCount = recipe.get().getRecipeLikesList().size();
        this.price = recipe.get().getPrice();

        Optional<RecipeLikes> foundRecipeLike = likesRepository.findByRecipeIdAndUserId(recipe.get().getId(), user.getId());
        this.likeStatus = foundRecipeLike.isPresent();
    }
}