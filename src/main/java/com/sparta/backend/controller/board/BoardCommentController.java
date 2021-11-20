package com.sparta.backend.controller.board;

import com.sparta.backend.dto.response.board.GetBoardCommentResponseDto;
import com.sparta.backend.dto.request.board.PostBoardCommentRequestDto;
import com.sparta.backend.dto.request.board.PutBoardCommentRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.board.BoardCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class BoardCommentController {

    private final BoardCommentService boardCommentService;

    //댓글 작성
    @PostMapping("/boards/comments")
    public ResponseEntity<?> createComment(@RequestBody PostBoardCommentRequestDto requestDto,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        GetBoardCommentResponseDto responseDto = boardCommentService.createComment(requestDto, userDetails);

        if(responseDto != null) {
            return new ResponseEntity<>(new CustomResponseDto<>(1, "댓글 작성 성공", responseDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CustomResponseDto<>(-1, "댓글 작성 실패", ""), HttpStatus.BAD_REQUEST);
        }

    }

    //댓글 조회
    @GetMapping("/boards/comments/{boardId}")
    public ResponseEntity<?> getComments(@PathVariable("boardId") Long id,
                                            @RequestParam int page, @RequestParam int size,
                                            @RequestParam boolean isAsc, @RequestParam String sortBy,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Page<GetBoardCommentResponseDto> responseDto =
                boardCommentService.getComments(id, page, size, isAsc, sortBy, userDetails);

        if(responseDto != null) {
            return new ResponseEntity<>(new CustomResponseDto<>(1, "댓글 조회 성공", responseDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CustomResponseDto<>(-1, "댓글 조회 실패", ""), HttpStatus.BAD_REQUEST);
        }
    }

    //댓글 수정
    @PutMapping("/boards/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable("commentId") Long id,
                                              @RequestBody PutBoardCommentRequestDto requestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        GetBoardCommentResponseDto responseDto = boardCommentService.updateComment(id, requestDto, userDetails);

        if(responseDto != null) {
            return new ResponseEntity<>(new CustomResponseDto<>(1, "댓글 수정 성공", responseDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CustomResponseDto<>(-1, "댓글 수정 실패", ""), HttpStatus.BAD_REQUEST);
        }

    }

    //댓글 삭제
    @DeleteMapping("/boards/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long id,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long boardCommentId = boardCommentService.deleteComment(id, userDetails);

        if(boardCommentId > 0) {
            return new ResponseEntity<>(new CustomResponseDto<>(1, "댓글 삭제 성공", ""), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CustomResponseDto<>(-1, "댓글 삭제 실패", ""), HttpStatus.BAD_REQUEST);
        }
    }
}
