package com.sparta.backend.controller.board;

import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.board.BoardLikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class BoardLikesController {

    private final BoardLikesService boardLikesService;

    //게시물 좋아요/취소
    @PostMapping("/boards/likes/{boardId}")
    public ResponseEntity<?> likeBoard(@PathVariable("boardId") Long id,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String boardLikesMessage = boardLikesService.likeBoard(id, userDetails);

        if(boardLikesMessage != null || boardLikesMessage.length() > 0) {
            return new ResponseEntity<>(new CustomResponseDto<>(1, boardLikesMessage, ""), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CustomResponseDto<>(-1, "게시물 좋아요/취소 실패", ""), HttpStatus.BAD_REQUEST);
        }
    }
}
