package com.sparta.backend.repository.user;

import com.sparta.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByKakaoId(Long kakaoId);

    @Query(value = "select nickname, image, count(cafe_like_id) cnt " +
            "from user u " +
            "join cafe c on u.user_id = c.user_id " +
            "join cafe_like cl on c.cafe_id = cl.cafe_id " +
            "where cl.reg_date between :startDate and :endDate " +
            "group by nickname " +
            "order by cnt desc limit 3",
            nativeQuery = true)
    List<Object[]> findTop3ByMostLiked(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query(value = "select nickname, image, count(cafe_id) cnt " +
            "from user u " +
            "join cafe c on u.user_id = c.user_id " +
            "where c.reg_date between :startDate and :endDate " +
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

    @Query(value = "select nickname, image, count(cafe_comment_id) cnt " +
            "from user u " +
            "join cafe_comment cc on u.user_id = cc.user_id " +
            "where cc.reg_date between :startDate and :endDate " +
            "group by nickname " +
            "order by cnt desc limit 3",
            nativeQuery = true)
    List<Object[]> findTop3ByMostComment(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    @Query(value =  "SELECT " +
                    "         u.user_id         AS user_id, " +
                    "         u.nickname        AS nickname, " +
                    "         u.image           AS profile, " +
                    "         COUNT(cl.user_id) AS like_count " +
                    "FROM     cafe_like cl, cafe c, user u " +
                    "WHERE    cl.cafe_id = c.cafe_id " +
                    "AND      c.user_id = u.user_id " +
                    "AND      cl.reg_date " +
                    "BETWEEN  :start AND :end " +
                    "AND      u.status = 'Y' " +
                    "GROUP BY u.user_id, u.nickname, u.image " +
                    "ORDER BY like_count DESC, user_id " +
                    "LIMIT    1", nativeQuery = true)
    List<Object[]> findTheMostLikedUser(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value =  "SELECT " +
                    "         u.user_id     AS user_id, " +
                    "         u.nickname    AS nickname, " +
                    "         u.image       AS profile, " +
                    "         COUNT(f.from_user_id) AS follow_count " +
                    "FROM     follow f, user u " +
                    "WHERE    f.to_user_id = u.user_id " +
                    "AND      f.reg_date " +
                    "BETWEEN  :start AND :end " +
                    "AND      u.status = 'Y' " +
                    "GROUP BY u.user_id, u.nickname, u.image " +
                    "ORDER BY follow_count DESC, user_id " +
                    "LIMIT    1 ", nativeQuery = true)
    List<Object[]> findTheMostFollowedUser(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    @Query(value =  "SELECT " +
                    "         u.user_id         AS user_id, " +
                    "         u.nickname        AS nickname, " +
                    "         u.image           AS profile, " +
                    "         COUNT(c.cafe_id)  AS post_count " +
                    "FROM     cafe c, user u " +
                    "WHERE    c.user_id = u.user_id " +
                    "AND      c.reg_date " +
                    "BETWEEN  :start AND :end " +
                    "AND      u.status = 'Y' " +
                    "GROUP BY u.user_id, u.nickname, u.image " +
                    "ORDER BY post_count DESC, user_id " +
                    "LIMIT    1", nativeQuery = true)
    List<Object[]> findTheMostWrotePostsUser(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    @Query(value =  "SELECT " +
                    "         u.user_id                 AS user_id, " +
                    "         u.nickname                AS nickname, " +
                    "         u.image                   AS profile, " +
                    "         COUNT(cc.cafe_comment_id) AS comment_count " +
                    "FROM     user u, cafe_comment cc " +
                    "WHERE    u.user_id = cc.user_id " +
                    "AND      cc.reg_date " +
                    "BETWEEN  :start AND :end " +
                    "AND      u.status = 'Y' " +
                    "GROUP BY u.user_id, u.nickname, u.image " +
                    "ORDER BY comment_count DESC, user_id " +
                    "LIMIT    1", nativeQuery = true)
    List<Object[]> findTheMostWroteCommentsUser(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Modifying
    @Transactional
    @Query(value = "update user set ranking_status = 0", nativeQuery = true)
    void initRankingStatus();
}
