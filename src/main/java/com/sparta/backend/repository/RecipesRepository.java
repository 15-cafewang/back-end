package com.sparta.backend.repository;

import com.sparta.backend.domain.Recipe.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipesRepository extends JpaRepository<Recipe,Long> {
    Page<Recipe> findAll(Pageable pageable);
}
