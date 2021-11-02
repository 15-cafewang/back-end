package com.sparta.backend.controller;

import com.sparta.backend.dto.request.board.PostBoardRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;

    //게시물 등록
    @PostMapping("/boards")
    public CustomResponseDto<?> createBoard(PostBoardRequestDto requestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

        Long boardId = boardService.createBoard(requestDto, userDetails);

        if(boardId > 0) {
            return new CustomResponseDto<>(1, "게시물 등록 완료", "");
        } else {
            return new CustomResponseDto<>(-1, "게시물 등록 실패", "");
        }
    }

}
