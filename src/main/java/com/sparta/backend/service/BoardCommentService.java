package com.sparta.backend.service;

import com.sparta.backend.domain.Board;
import com.sparta.backend.domain.BoardComment;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.response.board.GetBoardCommentResponseDto;
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
    public GetBoardCommentResponseDto createComment(PostBoardCommentRequestDto requestDto,
                              UserDetailsImpl userDetails) {
        Long boardId = requestDto.getBoardId();
        loginCheck(userDetails);    //로그인했는지 확인
        User currentLoginUser = userDetails.getUser();

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new NullPointerException("찾는 게시물이 없습니다.")
        );

        GetBoardCommentResponseDto responseDto = null;
        if(board != null) {
            BoardComment boardComment = new BoardComment(requestDto, board, currentLoginUser);
            BoardComment saveBoardComment = boardCommentRepository.save(boardComment);
            responseDto =
                    new GetBoardCommentResponseDto(saveBoardComment, boardCommentLikesRepository, userDetails);
        }

        return responseDto;
    }

    //댓글 조회
    public Page<GetBoardCommentResponseDto> getComments(Long id, int page, int size, boolean isAsc,
                                                        String sortBy, UserDetailsImpl userDetails) {
        loginCheck(userDetails);    //로그인했는지 확인
        //현재 페이지
        page = page - 1;
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
    public GetBoardCommentResponseDto updateComment(Long id, PutBoardCommentRequestDto requestDto, UserDetailsImpl userDetails) {
        loginCheck(userDetails);    //로그인했는지 확인
        Long currentLoginUser = userDetails.getUser().getId();

        BoardComment boardComment = boardCommentRepository.findById(id).orElseThrow(
                () -> new NullPointerException("찾는 댓글이 없습니다.")
        );

        GetBoardCommentResponseDto responseDto = null;
        if(boardComment != null) {
            Long writeUser = boardComment.getUser().getId();

            writterCheck(currentLoginUser, writeUser);  //작성자가 맞는지 확인
            BoardComment updateBoardComment = boardComment.updateComment(requestDto);
            responseDto =
                    new GetBoardCommentResponseDto(updateBoardComment, boardCommentLikesRepository, userDetails);
        }

        return responseDto;
    }

    //댓글 삭제
    @Transactional
    public Long deleteComment(Long id, UserDetailsImpl userDetails) {
        loginCheck(userDetails);    //로그인했는지 확인
        Long currentLoginUser = userDetails.getUser().getId();

        BoardComment boardComment = boardCommentRepository.findById(id).orElseThrow(
                () -> new NullPointerException("찾는 댓글이 없습니다.")
        );
        Long writeUser = boardComment.getUser().getId();

        writterCheck(currentLoginUser, writeUser);  //작성자가 맞는지 확인
        boardCommentRepository.deleteById(id);

        return id;
    }

    //로그인 되어있는지 확인하기
    private void loginCheck(UserDetailsImpl userDetails) {
        if(userDetails == null) {   //로그인 안 했을 떄
            throw new NullPointerException("로그인이 필요합니다.");
        }
    }

    //로그인한 계정이 작성자가 맞는지 확인하기
    private void writterCheck(Long currentLoginUserId, Long writeUserId) {
        if (!currentLoginUserId.equals(writeUserId)) {  //로그인한 계정이 작성자가 아닐 때
            throw new IllegalArgumentException("해당 댓글을 작성한 사용자만 수정 가능합니다.");
        }
    }
}
