package com.sparta.backend.repository;

import com.sparta.backend.domain.Board;
import com.sparta.backend.domain.BoardLikes;
import com.sparta.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardLikesRepository extends JpaRepository<BoardLikes, Long> {

    BoardLikes findByBoardAndUser(Board board, User user);

    void deleteByBoardAndUser(Board board, User user);

    Optional<BoardLikes> findByBoardIdAndUserId(Long boardId, Long userId);
}
