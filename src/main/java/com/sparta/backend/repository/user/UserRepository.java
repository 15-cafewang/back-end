package com.sparta.backend.repository.user;

import com.sparta.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByKakaoId(Long kakaoId);

    @Query(value = "select nickname, image, count(likes_id) cnt " +
            "from user u " +
            "join recipe r on u.user_id = r.user_id " +
            "join recipe_like rl on r.recipe_id = rl.recipe_id " +
            "where rl.reg_date between :startDate and :endDate " +
            "group by nickname " +
            "order by cnt desc limit 3",
            nativeQuery = true)
    List<Object[]> findTop3ByMostLiked(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query(value = "select nickname, image, count(recipe_id) cnt " +
            "from user u " +
            "join recipe r on u.user_id = r.user_id " +
            "where r.reg_date between :startDate and :endDate " +
            "group by nickname " +
            "order by cnt desc limit 3",
            nativeQuery = true)
    List<Object[]> findTop3ByMostRecipe(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Query(value = "select nickname, image, count(from_user_id) cnt " +
            "from user u " +
            "join follow f on u.user_id = f.to_user_id " +
            "where f.reg_date between :startDate and :endDate " +
            "group by nickname " +
            "order by cnt desc limit 3",
            nativeQuery = true)
    List<Object[]> findTop3ByMostFollow(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Query(value = "select nickname, image, count(comment_id) cnt " +
            "from user u " +
            "join recipe_comment rc on u.user_id = rc.user_id " +
            "where rc.reg_date between :startDate and :endDate " +
            "group by nickname " +
            "order by cnt desc limit 3",
            nativeQuery = true)
    List<Object[]> findTop3ByMostComment(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
}
