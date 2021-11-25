package com.sparta.backend.controller.board;

import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.board.BoardCommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BoardCommentLikesController {

    private final BoardCommentLikeService boardCommentLikeService;

    //댓글 좋아요/취소
    @PostMapping("/boards/comments/likes/{boardCommentId}")
    public ResponseEntity<?> likeBoardComment(@PathVariable("boardCommentId") Long id,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        checkLogin(userDetails);
        String boardCommentLikesMessage = boardCommentLikeService.likeBoardComment(id, userDetails);

        if(boardCommentLikesMessage != null || boardCommentLikesMessage.length() > 0) {
            return new ResponseEntity<>(new CustomResponseDto<>(1, boardCommentLikesMessage, ""), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CustomResponseDto<>(-1, "게시물 좋아요/취소 실패", ""), HttpStatus.BAD_REQUEST);
        }

    }

    //로그인 확인
    private void checkLogin(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new CustomErrorException("로그인된 유저만 사용가능한 기능입니다.");
        }
    }
}
