package com.sparta.backend.controller;

import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.BoardCommentLikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BoardCommentLikesController {

    private final BoardCommentLikesService boardCommentLikesService;

    //댓글 좋아요/취소
    @PostMapping("/boards/comments/likes/{boardCommentId}")
    public CustomResponseDto<?> likeBoardComment(@PathVariable("boardCommentId") Long id,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String boardCommentLikesMessage = boardCommentLikesService.likeBoardComment(id, userDetails);

        if(boardCommentLikesMessage != null || boardCommentLikesMessage.length() > 0) {
            return new CustomResponseDto<>(1, boardCommentLikesMessage, "");
        } else {
            return new CustomResponseDto<>(-1, "게시물 좋아요/취소 실패", "");
        }

    }
}
