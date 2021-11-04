package com.sparta.backend.repository;

import com.sparta.backend.domain.Board;
import com.sparta.backend.domain.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {
    List<BoardImage> findAllByBoard(Board board);
    void deleteAllByBoard(Board board);
}
