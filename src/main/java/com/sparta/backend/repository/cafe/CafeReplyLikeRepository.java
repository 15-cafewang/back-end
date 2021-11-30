package com.sparta.backend.repository.cafe;

import com.sparta.backend.domain.cafe.*;
import com.sparta.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CafeReplyLikeRepository extends JpaRepository<CafeReplyLike, Long> {

    Optional<CafeReplyLike> findByCafeReplyAndUser(CafeReply reply, User user);
}