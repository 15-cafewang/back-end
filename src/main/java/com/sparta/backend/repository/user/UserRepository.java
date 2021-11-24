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
                    "         COUNT(f.to_user_id) AS follow_count " +
                    "FROM     follow f, user u " +
                    "WHERE    f.from_user_id = u.user_id " +
                    "AND      f.reg_date BETWEEN :start AND :end " +
                    "AND     u.status = 'Y' " +
                    "GROUP BY f.from_user_id " +
                    "ORDER BY follow_count DESC, user_id " +
                    "LIMIT    1 ", nativeQuery = true)
    List<Object[]> findTheMostFollowedUser(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
