package com.sparta.backend.repository;

import com.sparta.backend.domain.recipe.Recipe;
import com.sparta.backend.domain.recipe.RecipeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecipeImageRepository extends JpaRepository<RecipeImage,Long> {
//    @Query("delete from RecipeImage i where i.image in :imageUrls")
    void deleteByImageIn(List<String> imageUrls);

    List<RecipeImage> findByImageIn(List<String> imageUrls);
}
