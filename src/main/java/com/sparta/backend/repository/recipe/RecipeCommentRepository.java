package com.sparta.backend.repository.recipe;

import com.sparta.backend.domain.recipe.RecipeComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RecipeCommentRepository extends JpaRepository<RecipeComment,Long> {
    List<RecipeComment> findAllByRecipeIdOrderByRegDateDesc(Long recipeId);

    Page<RecipeComment> findAllByRecipeId(Long recipeId, Pageable pageable);

    @Query(value =  "SELECT " +
                    "        u.user_id AS user_id, " +
                    "        u.nickname AS nickname, " +
                    "        u.image AS profile, " +
                    "        COUNT(rc.comment_id) AS comment_count " +
                    "FROM    user u, recipe_comment rc " +
                    "WHERE   u.user_id = rc.user_id " +
                    "AND     rc.reg_date BETWEEN :start AND :end " +
                    "AND     u.status = 'Y' " +
                    "GROUP BY user_id " +
                    "ORDER BY comment_count DESC, user_id " +
                    "LIMIT    1 ", nativeQuery = true)
    List<Object[]> findTheMostWroteCommentsUser(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
