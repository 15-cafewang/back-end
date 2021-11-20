package com.sparta.backend.controller.board;

import com.sparta.backend.domain.board.Board;
import com.sparta.backend.dto.request.board.PostBoardRequestDto;
import com.sparta.backend.dto.request.board.PutBoardRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.board.GetBoardDetailResponseDto;
import com.sparta.backend.dto.response.board.GetBoardResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;

    //게시물 등록
    @PostMapping("/boards")
    public ResponseEntity<?> createBoard(PostBoardRequestDto requestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

        Long boardId = boardService.createBoard(requestDto, userDetails);

        if(boardId > 0) {
            return new ResponseEntity<>(new CustomResponseDto<>(1, "게시물 등록 성공", ""), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CustomResponseDto<>(-1, "게시물 등록 실패", ""), HttpStatus.BAD_REQUEST);
        }
    }

    //전체 게시물 조회
    @GetMapping("/boards")
    public ResponseEntity<?> getBoards(@RequestParam int page, @RequestParam int size,
                                          @RequestParam boolean isAsc, @RequestParam String sortBy,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Page<GetBoardResponseDto> boardList =  boardService.getBoards(page, size, isAsc, sortBy, userDetails);

        if(boardList != null && boardList.getSize() > 0) {
            return new ResponseEntity<>(new CustomResponseDto<>(1, "전체 게시물 조회 성공", boardList), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CustomResponseDto<>(-1, "전체 게시물 조회 실패", ""), HttpStatus.BAD_REQUEST);
        }
    }

    //게시물 상세 조회
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<?> getBoardDetail(@PathVariable("boardId") Long id,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        GetBoardDetailResponseDto responseDto = boardService.getBoardDetail(id, userDetails);

        if(responseDto != null) {
            return new ResponseEntity<>(new CustomResponseDto<>(1, "게시물 상세 조회 성공", responseDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CustomResponseDto<>(-1, "게시물 상세 조회 실패", ""), HttpStatus.BAD_REQUEST);
        }
    }

    //게시물 수정
    @PutMapping("/boards/{boardId}")
    public ResponseEntity<?> updateBoard(@PathVariable("boardId") Long id,
                                            PutBoardRequestDto requestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Board board = boardService.updateBoard(id, requestDto, userDetails);
            if(board != null) {
                return new ResponseEntity<>(new CustomResponseDto<>(1, "게시물 수정 성공", ""), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new CustomResponseDto<>(-1, "게시물 수정 실패", ""), HttpStatus.BAD_REQUEST);
            }
        } catch(Exception exception) {
            return new ResponseEntity<>(new CustomResponseDto<>(-1, exception.getMessage(), ""), HttpStatus.BAD_REQUEST);
        }

    }

    //게시물 삭제
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable("boardId") Long id,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long boardId = boardService.deleteBoard(id, userDetails);

        if(boardId > 0) {
            return new ResponseEntity<>(new CustomResponseDto<>(1, "게시물 삭제 성공", ""), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CustomResponseDto<>(-1, "게시물 삭제 실패", ""), HttpStatus.BAD_REQUEST);
        }
    }

    //게시물 검색
    @GetMapping("/search/boards")
    public ResponseEntity<?> searchBoards(@RequestParam String keyword, @RequestParam int page, @RequestParam int size,
                                             @RequestParam boolean isAsc, @RequestParam String sortBy,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Page<GetBoardResponseDto> boardList =  boardService.searchBoards(keyword, page, size, isAsc, sortBy, userDetails);

        if(boardList != null && boardList.getSize() > 0) {
            return new ResponseEntity<>(new CustomResponseDto<>(1, "게시물 검색 성공", boardList), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CustomResponseDto<>(-1, "게시물 검색 실패", ""), HttpStatus.BAD_REQUEST);
        }

    }
}
