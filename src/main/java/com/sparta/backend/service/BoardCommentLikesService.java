package com.sparta.backend.service;

import com.sparta.backend.domain.BoardComment;
import com.sparta.backend.domain.BoardCommentLikes;
import com.sparta.backend.domain.User;
import com.sparta.backend.repository.BoardCommentLikesRepository;
import com.sparta.backend.repository.BoardCommentRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class BoardCommentLikesService {

    private final BoardCommentLikesRepository boardCommentLikesRepository;
    private final BoardCommentRepository boardCommentRepository;

    //게시물 댓글 좋아요/취소
    @Transactional
    public String likeBoardComment(Long id, UserDetailsImpl userDetails) {
        if(userDetails != null) {
            User user = userDetails.getUser();

            BoardComment boardComment = boardCommentRepository.findById(id).orElseThrow(
                    () -> new NullPointerException("찾는 댓글이 없습니다.")
            );

            BoardCommentLikes boardCommentLikes =
                    boardCommentLikesRepository.findByBoardCommentAndUser(boardComment, user);

            if(boardCommentLikes != null) {
                boardCommentLikesRepository.deleteByBoardCommentAndUser(boardComment, user);
                return "댓글에 좋아요 취소하였습니다.";
            } else {
                BoardCommentLikes newBoardCommentLikes = new BoardCommentLikes(user, boardComment);
                boardCommentLikesRepository.save(newBoardCommentLikes);
                return "댓글에 좋아요 하였습니다.";
            }

        } else {
            throw new NullPointerException("로그인이 필요합니다.");
        }
    }
}
