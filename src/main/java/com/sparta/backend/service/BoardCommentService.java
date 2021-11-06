package com.sparta.backend.service;

import com.sparta.backend.domain.Board;
import com.sparta.backend.domain.BoardComment;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.request.board.GetBoardCommentResponseDto;
import com.sparta.backend.dto.request.board.PostBoardCommentRequestDto;
import com.sparta.backend.dto.request.board.PutBoardCommentRequestDto;
import com.sparta.backend.repository.BoardCommentLikesRepository;
import com.sparta.backend.repository.BoardCommentRepository;
import com.sparta.backend.repository.BoardRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class BoardCommentService {

    private final BoardCommentRepository boardCommentRepository;
    private final BoardRepository boardRepository;
    private final BoardCommentLikesRepository boardCommentLikesRepository;

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

    //댓글 조회
    public Page<GetBoardCommentResponseDto> getComments(Long id, int page, int size, boolean isAsc,
                                                        String sortBy, UserDetailsImpl userDetails) {
        //정렬 기준
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        //어떤 컬럼 기준으로 정렬할 지 결정(sortBy: 컬럼이름)
        Sort sort = Sort.by(direction, sortBy);
        //페이징
        Pageable pageable = PageRequest.of(page, size, sort);

        Board board = boardRepository.findById(id).orElseThrow(
                () -> new NullPointerException("찾는 게시물이 없습니다.")
        );

        Page<BoardComment> boardCommentList = boardCommentRepository.findAllByBoard(board, pageable);

        Page<GetBoardCommentResponseDto> responseDtoList = boardCommentList.map(comment ->
                new GetBoardCommentResponseDto(comment, boardCommentLikesRepository, userDetails)
        );

        return responseDtoList;
    }

    //댓글 수정
    @Transactional
    public Long updateComment(Long id, PutBoardCommentRequestDto requestDto, UserDetailsImpl userDetails) {
        if(userDetails != null) {
            Long currentLoginUser = userDetails.getUser().getId();

            BoardComment boardComment = boardCommentRepository.findById(id).orElseThrow(
                    () -> new NullPointerException("찾는 댓글이 없습니다.")
            );
            Long writeUser = boardComment.getUser().getId();

            if(currentLoginUser.equals(writeUser)) {
                boardComment.updateComment(requestDto);
            } else {
                throw new IllegalArgumentException("작성자만 댓글을 수정할 수 있습니다.");
            }
        } else {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        return id;
    }

    //댓글 삭제
    @Transactional
    public Long deleteComment(Long id, UserDetailsImpl userDetails) {
        if(userDetails != null) {
            Long currentLoginUser = userDetails.getUser().getId();

            BoardComment boardComment = boardCommentRepository.findById(id).orElseThrow(
                    () -> new NullPointerException("찾는 댓글이 없습니다.")
            );
            Long writeUser = boardComment.getUser().getId();

            if(currentLoginUser.equals(writeUser)) {
                boardCommentRepository.deleteById(id);
            } else {
                throw new IllegalArgumentException("작성자만 댓글을 삭제할 수 있습니다.");
            }

        } else {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        return id;
    }
}
