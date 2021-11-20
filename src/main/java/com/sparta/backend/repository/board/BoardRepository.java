package com.sparta.backend.repository.board;

import com.sparta.backend.domain.board.Board;
import com.sparta.backend.domain.user.User;
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

    @Query("select b from Board b where b.id in (select bl.board.id from BoardLike bl where bl.user.id = :userId)")
    Page<Board> findAllByBoardLikesList(@Param("userId") Long userId, Pageable pageable);

    //전체 게시물 조회(인기순)
    @Query("select b from Board b left join b.boardLikeList bl group by b.id order by count(bl.user) desc")
    Page<Board> findBoardsOrderByLikeCountDesc(Pageable pageable);

    //검색(인기순)
    @Query("select b from Board b " +
           "left join b.boardLikeList bl " +
           "where b.title like %:keyword% or b.content like %:keyword% " +
           "group by b.id order by count(bl.user) desc")
    Page<Board> findBoardsByTitleContainingOrContentContainingOrderByLikeCountDesc(@Param("keyword") String keyword,
                                                                                   Pageable pageable);

}
