package com.sparta.backend.repository.recipe;

import com.sparta.backend.domain.recipe.RecipeDetailCount;
import com.sparta.backend.domain.recipe.RecipeSearchCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeSearchCountRepository extends JpaRepository<RecipeSearchCount,Long> {
}
