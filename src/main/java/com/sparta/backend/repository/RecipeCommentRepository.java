package com.sparta.backend.repository;

import com.sparta.backend.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeCommentRepository extends JpaRepository<Comment,Long> {
}
