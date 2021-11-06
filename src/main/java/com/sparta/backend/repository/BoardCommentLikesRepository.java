package com.sparta.backend.repository;

import com.sparta.backend.domain.BoardComment;
import com.sparta.backend.domain.BoardCommentLikes;
import com.sparta.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardCommentLikesRepository extends JpaRepository<BoardCommentLikes, Long> {
    BoardCommentLikes findByBoardCommentAndUser(BoardComment boardComment, User user);
    void deleteByBoardCommentAndUser(BoardComment boardComment, User user);
}
