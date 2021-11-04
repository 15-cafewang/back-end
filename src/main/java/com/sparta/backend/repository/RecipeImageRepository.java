package com.sparta.backend.repository;

import com.sparta.backend.domain.Recipe.Recipe;
import com.sparta.backend.domain.Recipe.RecipeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecipeImageRepository extends JpaRepository<RecipeImage,Long> {
//    @Query("delete r. from Recipe r where r.recipe_id = :recipe_id")
    void deleteAllByRecipe(Recipe recipe);
}
