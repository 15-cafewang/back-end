package com.sparta.backend.repository.board;

import com.sparta.backend.domain.board.BoardComment;
import com.sparta.backend.domain.board.BoardCommentLike;
import com.sparta.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCommentLikeRepository extends JpaRepository<BoardCommentLike, Long> {
    BoardCommentLike findByBoardCommentAndUser(BoardComment boardComment, User user);
    void deleteByBoardCommentAndUser(BoardComment boardComment, User user);
}
