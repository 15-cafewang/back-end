package com.sparta.backend.repository;

import com.sparta.backend.domain.Recipe.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecipeRepository extends JpaRepository<Recipe,Long> {
    Page<Recipe> findAll(Pageable pageable);

    @Query("select r from Recipe r join r.tagList t where t.name = :tagName")
    Page<Recipe> findAllByTag(String tagName, Pageable pageable);
}
