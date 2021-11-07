package com.sparta.backend.repository;

import com.sparta.backend.domain.Board;
import com.sparta.backend.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findAll(Pageable pageable);
    Page<Board> findAllByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    Page<Board> findAllByUser(Pageable pageable, User user);

    @Query("select b from Board b where b.id in (select bl.board.id from BoardLikes bl where bl.user.id = :userId)")
    Page<Board> findAllByBoardLikesList(@Param("userId") Long userId, Pageable pageable);
}
