package com.sparta.backend.repository;

import com.sparta.backend.domain.Recipe.Recipe;
import com.sparta.backend.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long> {
    List<Tag> findAllByRecipe(Recipe recipe);
    void deleteAllByRecipe(Recipe recipe);
}
