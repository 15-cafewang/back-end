package com.sparta.backend.repository.recipe;

import com.sparta.backend.domain.recipe.Recipe;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.queryInterface.PopularRecipeInterface;
import com.sparta.backend.dto.queryInterface.RecommendUserDataCheckDto;
import com.sparta.backend.dto.queryInterface.RecommendUserDataCheckInteface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Page<Recipe> findAll(Pageable pageable);

    @Query("select r from Recipe r join r.tagList t where t.name = :tagName")
    Page<Recipe> findAllByTag(String tagName, Pageable pageable);

    @Query("select r from Recipe r where r.title like concat('%',:keyword,'%') or r.content like concat('%',:keyword,'%')")
    Page<Recipe> findAllByTitleOrContent(String keyword, Pageable pageable);

    //레시피 목록조회 좋아요 순
    @Query("select r from Recipe r left join r.recipeLikesList l group by r.id order by count(l.recipe) desc")
    Page<Recipe> findRecipesOrderByLikeCountDesc(Pageable pageable);

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
    @Query(value = "SELECT r.recipe_id " +
            "FROM recipe r JOIN recipe_likes l ON r.recipe_id = l.recipe_id " +
            "WHERE l.reg_date BETWEEN :startDate AND :endDate " +
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

    //최근레시피(메인페이지) top4가져오기
    List<Recipe> findTop3ByOrderByRegDateDesc();

    @Query("select r from Recipe r left join r.recipeLikesList rl left join r.tagList tl " +
            "where tl.name = :keyword group by r.id order by count(rl.user) desc")
    Page<Recipe> findAllByTagOrderByLikeCount(String keyword, Pageable pageable);

    @Query("select r from Recipe r left join r.recipeLikesList rl " +
            "where r.title like %:keyword% or r.content like %:keyword% " +
            "group by r.id order by count(rl.user) desc ")
    Page<Recipe> findAllByTitleOrContentOrderByLikeCount(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "select exists (select * from tag t join recipe_likes rl on t.recipe_id = rl.recipe_id " +
            "               where rl.user_id = :userId " +
            "                 and rl.reg_date between :start and :end " +
            "               ) as exist_like, " +
            "       exists (select sc.keyword, count(sc.keyword) cnt from recipe_search_count sc " +
            "               where sc.user_id = :userId" +
            "                 and sc.reg_date between :start and :end " +
            "               group by sc.keyword) as exist_search, " +
            "       exists (select t.name, count(t.name) cnt from tag t join recipe_detail_count dc on t.recipe_id = dc.recipe_id " +
            "               where dc.user_id = :userId " +
            "               and dc.reg_date between :start and :end " +
            "               group by t.name) as exsit_detail;", nativeQuery = true)
    List<Object[]> checkUserHasData(Long userId, LocalDateTime start, LocalDateTime end);

    @Query(value = "select recipe_id from( " +
            "                 select rdc.recipe_id recipe_id, count(rdc.recipe_id) cnt from recipe_detail_count rdc join tag t on rdc.recipe_id = t.recipe_id " +
            "                 where t.name = ( " +
            "                     select name from " +
            "                         (select t.name name, count(t.name)*2 cnt from tag t join recipe_likes rl on t.recipe_id = rl.recipe_id " +
            "                          where rl.user_id = :userId " +
            "                            and rl.reg_date between :start and :end " +
            "                          group by t.name " +
            "                          union all " +
            "                          select sc.keyword name, count(sc.keyword) cnt from recipe_search_count sc " +
            "                          where user_id = :userId " +
            "                            and sc.reg_date between :start and :end " +
            "                          group by sc.keyword " +
            "                          union all " +
            "                          select t.name name, count(t.name) cnt from tag t join recipe_detail_count dc on t.recipe_id = dc.recipe_id " +
            "                          where dc.user_id = :userId and dc.reg_date between :start and :end " +
            "                          group by t.name) list " +
            "                     group by list.name " +
            "                     order by sum(list.cnt) desc limit 1 " +
            "                     ) " +
            "                 group by  t.name " +
            "                 union all " +
            "                 select  l.recipe_id recipe_id, count(l.recipe_id) cnt from recipe_likes l join tag t on l.recipe_id = t.recipe_id " +
            "                 where t.name = ( " +
            "                     select name from " +
            "                         (select t.name name, count(t.name)*2 cnt from tag t join recipe_likes rl on t.recipe_id = rl.recipe_id " +
            "                          where rl.user_id = :userId " +
            "                            and rl.reg_date between :start and :end " +
            "                          group by t.name " +
            "                          union all " +
            "                          select sc.keyword name, count(sc.keyword) cnt from recipe_search_count sc " +
            "                          where user_id = :userId " +
            "                            and sc.reg_date between :start and :end " +
            "                          group by sc.keyword " +
            "                          union all " +
            "                          select t.name name, count(t.name) cnt from tag t join recipe_detail_count dc on t.recipe_id = dc.recipe_id " +
            "                          where dc.user_id = :userId and dc.reg_date between :start and :end" +
            "                          group by t.name) list " +
            "                     group by list.name " +
            "                     order by sum(list.cnt) desc limit 1 " +
            "                 ) " +
            "                 group by t.name) list " +
            "order by cnt desc limit 1;", nativeQuery = true)
    List<Object[]> findRecommendedRecipeIdBasedOne(Long userId, LocalDateTime start, LocalDateTime end);

    @Query(value = "select r.recipe_id, t.name from recipe r " +
            "                           left join tag t on r.recipe_id= t.recipe_id " +
            "                           left join recipe_detail_count rdc on r.recipe_id= rdc.recipe_id " +
            "                           left join recipe_likes l on r.recipe_id = l.recipe_id " +

            "where t.name = ( " +
            "    select name from " +
            "        (select t.name, count(t.name)*2 cnt from tag t join recipe_likes rl on t.recipe_id = rl.recipe_id " +
            "         where rl.reg_date between :start and :end " +
            "         group by t.name " +
            "         union all " +
            "         select sc.keyword, count(sc.keyword) cnt from recipe_search_count sc " +
            "         where sc.reg_date between :start and :end " +
            "         group by sc.keyword " +
            "         union all " +
            "         select t.name, count(t.name) cnt from tag t join recipe_detail_count dc on t.recipe_id = dc.recipe_id " +
            "         where dc.reg_date between :start and :end " +
            "         group by t.name) list " +
            "    group by list.name " +
            "    order by  SUM(cnt) desc limit 1 " +
            ") " +
            "  and (rdc.reg_date between :start and :end " +
            "  or l.reg_date between :start and :end) " +
            "group by r.recipe_id limit 1", nativeQuery = true)
    List<Object[]> findRecommendedRecipeIdBasedAll(LocalDateTime start, LocalDateTime end);

    @Query(value =
            "select name from " +
                    "    (select t.name name, count(t.name)*2 cnt from tag t join recipe_likes rl on t.recipe_id = rl.recipe_id " +
                    "     where rl.user_id = :userId " +
                    "       and rl.reg_date between :start and :end " +
                    "     group by t.name " +
                    "     union all " +
                    "     select sc.keyword name, count(sc.keyword) cnt from recipe_search_count sc " +
                    "     where user_id = :userId " +
                    "       and sc.reg_date between :start and :end " +
                    "     group by sc.keyword " +
                    "     union all " +
                    "     select t.name name, count(t.name) cnt from tag t join recipe_detail_count dc on t.recipe_id = dc.recipe_id " +
                    "     where dc.user_id = :userId and dc.reg_date between :start and :end " +
                    "     group by t.name) list " +
                    "group by list.name " +
                    "order by sum(list.cnt) desc limit 1;"
            , nativeQuery = true)
    String findRecommendingTagNameBasedOne(Long userId, LocalDateTime start, LocalDateTime end);

    @Query(value =
            "select name from " +
                    "    (select t.name name, count(t.name)*2 cnt from tag t join recipe_likes rl on t.recipe_id = rl.recipe_id " +
                    "     where rl.reg_date between :start and :end " +
                    "     group by t.name " +
                    "     union all " +
                    "     select sc.keyword name, count(sc.keyword) cnt from recipe_search_count sc " +
                    "     where sc.reg_date between :start and :end " +
                    "     group by sc.keyword " +
                    "     union all " +
                    "     select t.name name, count(t.name) cnt from tag t join recipe_detail_count dc on t.recipe_id = dc.recipe_id " +
                    "     where dc.reg_date between :start and :end " +
                    "     group by t.name) list " +
                    "group by list.name " +
                    "order by sum(list.cnt) desc limit 1;"
            , nativeQuery = true)
    String findRecommendingTagNameBasedAll(LocalDateTime start, LocalDateTime end);

    @Query(value ="select list.recipe_id from( " +
            "                 select rdc.recipe_id recipe_id, count(rdc.recipe_id) cnt from recipe_detail_count rdc join tag t on rdc.recipe_id = t.recipe_id " +
            "                 where t.name = :foundTagName " +
            "                 group by  t.name " +
            "                 union all " +
            "                 select  l.recipe_id recipe_id, count(l.recipe_id) cnt from recipe_likes l join tag t on l.recipe_id = t.recipe_id " +
            "                 where t.name = :foundTagName " +
            "                 group by t.name) list " +
            "                 join tag t on list.recipe_id= t.recipe_id " +
            "order by cnt desc limit 1;", nativeQuery = true)
    Long findRecommendingRecipeIdByTagName(String foundTagName);

    @Query(value = "select * from recipe order by rand() limit 1"
    ,nativeQuery = true)
    Recipe findRandomRecipe();
}
