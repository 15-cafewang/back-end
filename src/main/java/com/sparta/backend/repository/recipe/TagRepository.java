package com.sparta.backend.repository.recipe;

import com.sparta.backend.domain.recipe.Recipe;
import com.sparta.backend.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long> {
    List<Tag> findAllByRecipe(Recipe recipe);
    void deleteAllByRecipe(Recipe recipe);

    @Query("select t from RecipeDetailCount dc, Tag t where dc.user.id = :userId and dc.recipe = t.recipe group by t.name order by count(t.name) desc ")
    List<Tag> findRecommendedTag(@Param("userId") Long userId);
}
