package com.sparta.backend.repository;

import com.sparta.backend.domain.Recipe;
import com.sparta.backend.domain.RecipeLikes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeLikesRepository extends JpaRepository<RecipeLikes,Long> {

     Optional<RecipeLikes> findByRecipeIdAndUserId(Long recipeId, Long userId);
}
