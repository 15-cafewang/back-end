package com.sparta.backend.repository.cafe;

import com.sparta.backend.domain.cafe.Cafe;
import com.sparta.backend.domain.cafe.CafeLike;
import com.sparta.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CafeLikeRepository extends JpaRepository<CafeLike,Long> {

     Optional<CafeLike> findByCafeIdAndUserId(Long cafeId, Long userId);

     Optional<CafeLike> findByCafeAndUser(Cafe cafe, User user);
}
