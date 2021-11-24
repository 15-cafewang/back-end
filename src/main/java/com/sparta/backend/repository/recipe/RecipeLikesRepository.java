package com.sparta.backend.repository.recipe;

import com.sparta.backend.domain.recipe.Recipe;
import com.sparta.backend.domain.recipe.RecipeLike;
import com.sparta.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeLikesRepository extends JpaRepository<RecipeLike,Long> {

     Optional<RecipeLike> findByRecipeIdAndUserId(Long recipeId, Long userId);

     Optional<RecipeLike> findByRecipeAndUser(Recipe recipe, User user);
}
