package com.sparta.backend.controller.cafe;

import com.sparta.backend.domain.cafe.CafeComment;
import com.sparta.backend.dto.request.cafe.CafeCommentRequestDto;
import com.sparta.backend.dto.request.cafe.CafeCommentUpdateRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.cafe.CafeCommentResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.cafe.CafeCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class CafeCommentController {

    private final CafeCommentService commentService;

    //댓글 입력
    @PostMapping("/cafes/comment")
    public ResponseEntity<?> postComment(@RequestBody CafeCommentRequestDto requestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        CafeCommentResponseDto responseDto = commentService.saveComment(requestDto,userDetails);
        return new ResponseEntity<>(new CustomResponseDto<>(1, "댓글 등록 성공!", responseDto),HttpStatus.OK);
    }

    //댓글들 조회
//    @GetMapping("/recipes/comment/{recipeId}")
//    public CustomResponseDto<?> getComment(@PathVariable Long recipeId,
//                                           @AuthenticationPrincipal UserDetailsImpl userDetails){
//        List<RecipeCommentResponseDto> responseDtoList = commentService.getComment(recipeId, userDetails);
//        return new CustomResponseDto<>(1,"댓글 조회 성공!",responseDtoList);
//    }

    //댓글들 페이지로 조회
    @GetMapping("/cafes/comment/{cafeId}")
    public ResponseEntity<?> getComment(@PathVariable Long cafeId,
                                           @RequestParam("page") int page,
                                           @RequestParam("size") int size,
                                           @RequestParam("isAsc") boolean isAsc,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        page = page-1;
        Page<CafeCommentResponseDto> responseDtoList = commentService.getCommentByPage(cafeId, page, size, isAsc, userDetails);
        return new ResponseEntity<>(new CustomResponseDto<>(1,"댓글 조회 성공!",responseDtoList),HttpStatus.OK);
    }

    //댓글 삭제
    @DeleteMapping("/cafes/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        checkOwnership(commentId,userDetails);
        commentService.deleteComment(commentId, userDetails);
        return new ResponseEntity<>(new CustomResponseDto<>(1,"댓글 삭제 성공!", ""),HttpStatus.OK);
    }

    //댓글 수정
    @PutMapping("/cafes/comment/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId,
                                              @RequestBody CafeCommentUpdateRequestDto updateRequestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        checkOwnership(commentId,userDetails);
        CafeCommentResponseDto responseDto = commentService.updateComment(commentId, updateRequestDto, userDetails);
        return new ResponseEntity<>(new CustomResponseDto<>(1,"댓글 수정 성공", responseDto),HttpStatus.OK);
   }

   //댓글 좋아요 등록/취소
    @GetMapping("/cafes/comment/likes/{commentId}")
    public ResponseEntity<?> likeComment(@PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        String resultMessage = commentService.likeComment(commentId, userDetails.getUser());
        return new ResponseEntity<>(new CustomResponseDto<>(1,resultMessage, ""),HttpStatus.OK);
    }


    private void checkLogin(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new CustomErrorException("로그인된 유저만 사용가능한 기능입니다.");
        }
    }
    private void checkOwnership(Long commentId, UserDetailsImpl userDetails){
        Optional<CafeComment> cafe = commentService.findById(commentId);
        if(!cafe.get().getUser().getEmail().equals(userDetails.getUser().getEmail())) throw new CustomErrorException("본인의 게시물만 수정,삭제 가능합니다.");
    }
}