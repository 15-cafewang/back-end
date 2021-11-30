package com.sparta.backend.service.board;

import com.sparta.backend.domain.board.BoardComment;
import com.sparta.backend.domain.board.BoardCommentLike;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.repository.board.BoardCommentLikeRepository;
import com.sparta.backend.repository.board.BoardCommentRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class BoardCommentLikeService {

    private final BoardCommentLikeRepository boardCommentLikeRepository;
    private final BoardCommentRepository boardCommentRepository;

    //게시물 댓글 좋아요/취소
    @Transactional
    public String likeBoardComment(Long id, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        BoardComment boardComment = boardCommentRepository.findById(id).orElseThrow(
                () -> new CustomErrorException("해당 댓글이 존재하지 않습니다")
        );

        BoardCommentLike boardCommentLike =
                boardCommentLikeRepository.findByBoardCommentAndUser(boardComment, user);

        if(boardCommentLike != null) {
            boardCommentLikeRepository.deleteByBoardCommentAndUser(boardComment, user);
            return "댓글에 좋아요 취소하였습니다.";
        } else {
            BoardCommentLike newBoardCommentLike = new BoardCommentLike(user, boardComment);
            boardCommentLikeRepository.save(newBoardCommentLike);
            return "댓글에 좋아요 하였습니다.";
        }
    }
}
