package com.sparta.backend.repository;

import com.sparta.backend.domain.Recipe.Recipe;
import com.sparta.backend.dto.queryInterface.PopularRecipeInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe,Long> {
    Page<Recipe> findAll(Pageable pageable);

    @Query("select r from Recipe r join r.tagList t where t.name = :tagName")
    Page<Recipe> findAllByTag(String tagName, Pageable pageable);

    @Query("select r from Recipe r where r.title like concat('%',:keyword,'%') or r.content like concat('%',:keyword,'%')")
    Page<Recipe> findAllByTitleOrContent(String keyword, Pageable pageable);

    @Query(value = "select r.id as recipeId, count(l.recipe) as likeCount,r.title as title, r.content as content , r.price as price " +
            "from Recipe r join r.recipeLikesList l " +
            "group by r.id order by count(l.recipe) desc ")
    List<PopularRecipeInterface> findPopularRecipe();
}
