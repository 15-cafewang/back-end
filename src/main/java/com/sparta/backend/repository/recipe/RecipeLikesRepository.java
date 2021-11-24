package com.sparta.backend.repository.recipe;

import com.sparta.backend.domain.recipe.Recipe;
import com.sparta.backend.domain.recipe.RecipeLike;
import com.sparta.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecipeLikesRepository extends JpaRepository<RecipeLike,Long> {

     Optional<RecipeLike> findByRecipeIdAndUserId(Long recipeId, Long userId);

     Optional<RecipeLike> findByRecipeAndUser(Recipe recipe, User user);

     @Query(value = "SELECT " +
                    "         u.user_id AS user_id, " +
                    "         u.nickname AS nickname, " +
                    "         u.image AS profile, " +
                    "         COUNT(rl.user_id) AS like_count " +
                    "FROM     recipe_like rl, recipe r, user u " +
                    "WHERE    rl.recipe_id = r.recipe_id " +
                    "AND      r.user_id = u.user_id " +
                    "AND      rl.reg_date BETWEEN :start AND :end " +
                    "AND     u.status = 'Y' " +
                    "GROUP BY rl.recipe_id " +
                    "ORDER BY like_count DESC, user_id " +
                    "LIMIT    1 ", nativeQuery = true)
     List<Object[]> findTheMostLikedUser(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
