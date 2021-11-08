package com.sparta.backend.repository;

import com.sparta.backend.domain.Follow;
import com.sparta.backend.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFromUserAndToUser(User fromUser, User toUser);

    // 내가 팔로우하는 유저들
    List<Follow> findAllByFromUser(User fromUser);

    // 나를 팔로우하는 유저들
    List<Follow> findAllByToUser(User toUser);

    // 팔로잉 목록 조회
    Page<Follow> findAllByFromUser(Pageable pageable, User fromUser);
}
