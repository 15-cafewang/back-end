package com.sparta.backend.repository.board;

import com.sparta.backend.domain.board.Board;
import com.sparta.backend.domain.board.BoardComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    Page<BoardComment> findAllByBoard(Board board, Pageable pageable);
}
