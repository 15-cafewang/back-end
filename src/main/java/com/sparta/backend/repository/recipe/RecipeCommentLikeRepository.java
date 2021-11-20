package com.sparta.backend.repository.recipe;

import com.sparta.backend.domain.recipe.RecipeComment;
import com.sparta.backend.domain.recipe.RecipeCommentLike;
import com.sparta.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeCommentLikeRepository extends JpaRepository<RecipeCommentLike,Long> {

    Optional<RecipeCommentLike> findByRecipeCommentAndUser(RecipeComment comment, User user);
}
