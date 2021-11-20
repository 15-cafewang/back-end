package com.sparta.backend.repository.board;

import com.sparta.backend.domain.board.Board;
import com.sparta.backend.domain.board.BoardLike;
import com.sparta.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardLikesRepository extends JpaRepository<BoardLike, Long> {

    BoardLike findByBoardAndUser(Board board, User user);

    void deleteByBoardAndUser(Board board, User user);

    Optional<BoardLike> findByBoardIdAndUserId(Long boardId, Long userId);
}
