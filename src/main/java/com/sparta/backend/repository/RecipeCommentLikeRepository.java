package com.sparta.backend.repository;

import com.sparta.backend.domain.Recipe.RecipeComment;
import com.sparta.backend.domain.Recipe.RecipeCommentLikes;
import com.sparta.backend.domain.Recipe.RecipeLikes;
import com.sparta.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeCommentLikeRepository extends JpaRepository<RecipeCommentLikes,Long> {

    Optional<RecipeCommentLikes> findByRecipeCommentAndUser(RecipeComment comment, User user);
}
