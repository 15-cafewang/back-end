package com.sparta.backend.repository.recipe;

import com.sparta.backend.domain.recipe.RecipeComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeCommentRepository extends JpaRepository<RecipeComment,Long> {
    List<RecipeComment> findAllByRecipeIdOrderByRegDateDesc(Long recipeId);

    Page<RecipeComment> findAllByRecipeId(Long recipeId, Pageable pageable);

}
