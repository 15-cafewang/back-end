package com.sparta.backend.repository;

import com.sparta.backend.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeCommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllByRecipeIdOrderByRegDateDesc(Long recipeId);
}
