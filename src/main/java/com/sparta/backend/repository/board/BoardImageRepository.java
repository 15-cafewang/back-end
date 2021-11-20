package com.sparta.backend.repository.board;

import com.sparta.backend.domain.board.Board;
import com.sparta.backend.domain.board.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {
    List<BoardImage> findAllByBoard(Board board);
    List<BoardImage> findAllByImageIn(List<String> deleteImage);
    void deleteByImageIn(List<String> deleteImage);
    void deleteAllByBoard(Board board);
}
