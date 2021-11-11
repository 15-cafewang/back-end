package com.sparta.backend.dto.response.userinfo;

import com.sparta.backend.domain.recipe.Recipe;
import com.sparta.backend.domain.recipe.RecipeLikes;
import com.sparta.backend.repository.RecipeLikesRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetRecipeListResponseDto {

    private Long recipeId;
    private String title;
    private String nickname;
    private int price;
    private List<String> imageList = new ArrayList<>();
    private int likeCount;
    private boolean likeStatus;

    public GetRecipeListResponseDto(Recipe recipe,
                                    UserDetailsImpl userDetails,
                                    RecipeLikesRepository recipeLikesRepository) {

        this.recipeId = recipe.getId();
        this.title = recipe.getTitle();
        this.nickname = recipe.getUser().getNickname();
        this.price = recipe.getPrice();
        recipe.getRecipeImagesList().forEach(RecipeImage -> this.imageList.add(RecipeImage.getImage()));
        this.likeCount = recipe.getRecipeLikesList().size();

        Optional<RecipeLikes> foundRecipeLike = recipeLikesRepository
                .findByRecipeIdAndUserId(recipe.getId(), userDetails.getUser().getId());
        this.likeStatus = foundRecipeLike.isPresent();
    }

}
