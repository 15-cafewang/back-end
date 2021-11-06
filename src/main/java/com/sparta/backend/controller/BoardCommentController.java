package com.sparta.backend.controller;

import com.sparta.backend.dto.request.board.PostBoardCommentRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.BoardCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BoardCommentController {

    private final BoardCommentService boardCommentService;

    //댓글 작성
    @PostMapping("/boards/comments")
    public CustomResponseDto<?> createComment(@RequestBody PostBoardCommentRequestDto requestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long commentId = boardCommentService.createComment(requestDto, userDetails);

        if(commentId > 0) {
            return new CustomResponseDto<>(1, "댓글 작성 성공", "");
        } else {
            return new CustomResponseDto<>(-1, "댓글 작성 실패", "");
        }

    }
}
