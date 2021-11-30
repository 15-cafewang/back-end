package com.sparta.backend.repository.cafe;

import com.sparta.backend.domain.cafe.CafeComment;
import com.sparta.backend.domain.cafe.CafeCommentLike;
import com.sparta.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CafeCommentLikeRepository extends JpaRepository<CafeCommentLike,Long> {

    Optional<CafeCommentLike> findByCafeCommentAndUser(CafeComment comment, User user);
}
