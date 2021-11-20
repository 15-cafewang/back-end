package com.sparta.backend.controller;

import com.sparta.backend.dto.response.board.GetBoardCommentResponseDto;
import com.sparta.backend.dto.request.board.PostBoardCommentRequestDto;
import com.sparta.backend.dto.request.board.PutBoardCommentRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.BoardCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class BoardCommentController {

    private final BoardCommentService boardCommentService;

    //댓글 작성
    @PostMapping("/boards/comments")
    public CustomResponseDto<?> createComment(@RequestBody PostBoardCommentRequestDto requestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        GetBoardCommentResponseDto responseDto = boardCommentService.createComment(requestDto, userDetails);

        if(responseDto != null) {
            return new CustomResponseDto<>(1, "댓글 작성 성공", responseDto);
        } else {
            return new CustomResponseDto<>(-1, "댓글 작성 실패", "");
        }

    }

    //댓글 조회
    @GetMapping("/boards/comments/{boardId}")
    public CustomResponseDto<?> getComments(@PathVariable("boardId") Long id,
                                            @RequestParam int page, @RequestParam int size,
                                            @RequestParam boolean isAsc, @RequestParam String sortBy,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Page<GetBoardCommentResponseDto> responseDto =
                boardCommentService.getComments(id, page, size, isAsc, sortBy, userDetails);

        if(responseDto != null) {
            return new CustomResponseDto<>(1, "댓글 조회 성공", responseDto);
        } else {
            return new CustomResponseDto<>(-1, "댓글 조회 실패", "");
        }
    }

    //댓글 수정
    @PutMapping("/boards/comments/{commentId}")
    public CustomResponseDto<?> updateComment(@PathVariable("commentId") Long id,
                                              @RequestBody PutBoardCommentRequestDto requestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        GetBoardCommentResponseDto responseDto = boardCommentService.updateComment(id, requestDto, userDetails);

        if(responseDto != null) {
            return new CustomResponseDto<>(1, "댓글 수정 성공", responseDto);
        } else {
            return new CustomResponseDto<>(-1, "댓글 수정 실패", "");
        }

    }

    //댓글 삭제
    @DeleteMapping("/boards/comments/{commentId}")
    public CustomResponseDto<?> deleteComment(@PathVariable("commentId") Long id,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long boardCommentId = boardCommentService.deleteComment(id, userDetails);

        if(boardCommentId > 0) {
            return new CustomResponseDto<>(1, "댓글 삭제 성공", "");
        } else {
            return new CustomResponseDto<>(-1, "댓글 삭제 실패", "");
        }
    }
}
