package com.sparta.backend.repository;

import com.sparta.backend.domain.Recipe.Recipe;
import com.sparta.backend.domain.Recipe.RecipeLikes;
import com.sparta.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeLikesRepository extends JpaRepository<RecipeLikes,Long> {

     Optional<RecipeLikes> findByRecipeIdAndUserId(Long recipeId, Long userId);

     Optional<RecipeLikes> findByRecipeAndUser(Recipe recipe, User user);
}
