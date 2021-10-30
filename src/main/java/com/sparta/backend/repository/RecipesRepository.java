package com.sparta.backend.repository;

import com.sparta.backend.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipesRepository extends JpaRepository<Recipe,Long> {
}
