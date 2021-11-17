package com.sparta.backend.repository;

import com.sparta.backend.domain.recipe.RecipeDetailCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeDetailCountRepository extends JpaRepository<RecipeDetailCount,Long> {
}
