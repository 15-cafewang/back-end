package com.sparta.backend.repository.recipe;

import com.sparta.backend.domain.Tag;
import com.sparta.backend.domain.recipe.Recipe;
import com.sparta.backend.domain.recipe.RecipeDetailCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeDetailCountRepository extends JpaRepository<RecipeDetailCount,Long> {


}
