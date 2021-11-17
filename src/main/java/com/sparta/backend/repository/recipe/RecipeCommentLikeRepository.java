package com.sparta.backend.repository.recipe;

import com.sparta.backend.domain.recipe.RecipeComment;
import com.sparta.backend.domain.recipe.RecipeCommentLikes;
import com.sparta.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeCommentLikeRepository extends JpaRepository<RecipeCommentLikes,Long> {

    Optional<RecipeCommentLikes> findByRecipeCommentAndUser(RecipeComment comment, User user);
}
