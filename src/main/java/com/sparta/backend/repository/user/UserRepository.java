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

    @Query(value = "SELECT " +
                    "         u.user_id AS user_id, " +
                    "         u.nickname AS nickname, " +
                    "         u.image AS profile, " +
                    "         COUNT(f.to_user_id) AS follow_count " +
                    "FROM     follow f, user u " +
                    "WHERE    f.from_user_id = u.user_id " +
                    "AND      f.reg_date BETWEEN :start AND :end " +
                    "AND     u.status = 'Y' " +
                    "GROUP BY f.from_user_id " +
                    "ORDER BY follow_count DESC, user_id " +
                    "LIMIT    1 ", nativeQuery = true)
    List<Object[]> findTheMostFollowedUser(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    @Query(value = "SELECT " +
            "        u.user_id AS user_id, " +
            "        u.nickname AS nickname, " +
            "        u.image AS profile, " +
            "        COUNT(r.recipe_id) AS post_count " +
            "FROM    recipe r, user u " +
            "WHERE   r.user_id = u.user_id " +
            "AND     r.reg_date BETWEEN :start AND :end " +
            "AND     u.status = 'Y' " +
            "GROUP BY user_id " +
            "ORDER BY post_count DESC, user_id " +
            "LIMIT    1 ", nativeQuery = true)
    List<Object[]> findTheMostWrotePostsUser(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


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
