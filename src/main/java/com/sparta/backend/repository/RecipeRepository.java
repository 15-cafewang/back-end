package com.sparta.backend.repository;

import com.sparta.backend.domain.Recipe.Recipe;
import com.sparta.backend.domain.Recipe.RecipeLikes;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.queryInterface.PopularRecipeInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe,Long> {
    Page<Recipe> findAll(Pageable pageable);

    @Query("select r from Recipe r join r.tagList t where t.name = :tagName")
    Page<Recipe> findAllByTag(String tagName, Pageable pageable);

    @Query("select r from Recipe r where r.title like concat('%',:keyword,'%') or r.content like concat('%',:keyword,'%')")
    Page<Recipe> findAllByTitleOrContent(String keyword, Pageable pageable);

//  특정기간&인기레시피 - 원하는 칼럼만 가져오는 jpql
    @Query("select r.id as recipeId, r.title as title, r.content as content , r.price as price " +
            "from Recipe r join r.recipeLikesList l " +
            "where l.regDate between :startDate and :endDate " +
            "group by r.id order by count(l.recipe) desc ")
    List<PopularRecipeInterface> findPopularRecipe(LocalDateTime startDate, LocalDateTime endDate);

    //특정기간&인기레시피 - id만 가져오기..전체..jpql
    @Query("select r.id " +
            "from Recipe r join r.recipeLikesList l " +
            "where l.regDate between :startDate and :endDate " +
            "group by r.id order by count(l.recipe) ")
    List<Long> findPopularRecipeId(LocalDateTime startDate, LocalDateTime endDate);

    //특정기간&인기레시피 - id만 가져오기..top3..native sql
    @Query(value="SELECT r.recipe_id " +
            "FROM recipe r JOIN recipe_likes l ON r.recipe_id = l.recipe_id " +
            "WHERE l.regDate BETWEEN :startDate AND :endDate " +
            "GROUP BY r.recipe_id order by count(l.recipe_id) desc limit 3",
    nativeQuery = true)
    List<Long> findPopularRecipeId2(LocalDateTime startDate, LocalDateTime endDate);

    //한번에 좋아요 순으로 Recipe객체 가져오려는 시도 실패
//    @Query(value = "select re from Recipe re where re.id in (" +
//            "select r.id as recipeId " +
//            "from Recipe r join r.recipeLikesList l " +
//            "where l.regDate between :startDate and :endDate " +
//            "group by r.id order by count(l.recipe) desc) " +
//            "order by field(re.id, " +
//            "select r.id as recipeId " +
//            "from Recipe r join r.recipeLikesList l " +
//            "where l.regDate between :startDate and :endDate " +
//            "group by r.id order by count(l.recipe) desc) ")
//    List<Recipe> findPopularRecipe2(LocalDateTime startDate, LocalDateTime endDate);

    //위와 같은 시도 sql로 하려는 시도 실패
//    @Query(value = "SELECT * from recipe r2 " +
//            "where r2.recipe_id " +
//            "in " +
//            "(SELECT r.recipe_id " +
//            "FROM recipe r JOIN recipe_likes l ON r.recipe_id = l.recipe_id " +
//            "WHERE l.regDate BETWEEN '2021-11-01' AND '2021-11-08' " +
//            "GROUP BY r.recipe_id order by count(l.recipe_id) desc) " +
//            "ORDER BY FIELD(r2.recipe_id,(SELECT r.recipe_id " +
//            "                             FROM recipe r JOIN recipe_likes l ON r.recipe_id = l.recipe_id " +
//            "                             WHERE l.regDate BETWEEN :startDate AND :endDate " +
//            "                             GROUP BY r.recipe_id order by count(l.recipe_id) desc))"
//    ,nativeQuery = true)
//    List<Recipe> findPopularRecipe3(LocalDateTime startDate, LocalDateTime endDdate);

    Page<Recipe> findAllByUser(Pageable pageable, User user);

    @Query("select r from Recipe r where r.id in (select rl.recipe.id from RecipeLikes rl where rl.user.id = :userId)")
    Page<Recipe> findAllByRecipeLikesList(@Param("userId") Long userId, Pageable pageable);
}
