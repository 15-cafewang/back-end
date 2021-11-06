package com.sparta.backend.service;

import com.sparta.backend.domain.Board;
import com.sparta.backend.domain.BoardComment;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.request.board.PostBoardCommentRequestDto;
import com.sparta.backend.repository.BoardCommentRepository;
import com.sparta.backend.repository.BoardRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class BoardCommentService {

    private final BoardCommentRepository boardCommentRepository;
    private final BoardRepository boardRepository;

    //댓글 작성
    @Transactional
    public Long createComment(PostBoardCommentRequestDto requestDto,
                              UserDetailsImpl userDetails) {
        Long boardId = requestDto.getBoardId();
        Long commentId = 0L;
        if(userDetails != null) {
            User currentLoginUser = userDetails.getUser();

            Board board = boardRepository.findById(boardId).orElseThrow(
                    () -> new NullPointerException("찾는 게시물이 없습니다.")
            );

            if(board != null) {
                BoardComment boardComment = new BoardComment(requestDto, board, currentLoginUser);
                BoardComment saveBoardComment = boardCommentRepository.save(boardComment);
                commentId = saveBoardComment.getId();
            }
        } else {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        return commentId;
    }
}
