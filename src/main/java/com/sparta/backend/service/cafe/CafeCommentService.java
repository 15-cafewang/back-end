package com.sparta.backend.service.cafe;

import com.sparta.backend.domain.cafe.CafeComment;
import com.sparta.backend.domain.cafe.Cafe;
import com.sparta.backend.domain.cafe.CafeCommentLike;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.dto.request.cafe.CafeCommentRequestDto;
import com.sparta.backend.dto.request.cafe.CafeCommentUpdateRequestDto;
import com.sparta.backend.dto.response.cafe.CafeCommentResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.repository.cafe.CafeCommentLikeRepository;
import com.sparta.backend.repository.cafe.CafeCommentRepository;
import com.sparta.backend.repository.cafe.CafeRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CafeCommentService {
    private final CafeCommentRepository commentRepository;
    private final CafeRepository cafeRepository;
    private final CafeCommentLikeRepository commentLikeReposiotory;

    //댓글 저장
    public CafeCommentResponseDto saveComment(CafeCommentRequestDto requestDto, UserDetailsImpl userDetails) {
        Cafe cafe = cafeRepository.findById(requestDto.getCafeId()).orElseThrow(()->
                new CustomErrorException("해당 댓글의 레시피가 존재하지 않습니다."));
        CafeComment cafeComment = new CafeComment(requestDto.getContent(),userDetails.getUser(), cafe);
        CafeComment savedComment = commentRepository.save(cafeComment);
        CafeCommentResponseDto responseDto = new CafeCommentResponseDto(savedComment, userDetails, commentLikeReposiotory);
        return responseDto;
    }

    //레시피에 대한 댓글들 조회- 리스트로 리턴
//    public List<CafeCommentResponseDto> getComment(Long cafeId, UserDetailsImpl userDetails){
//        List<CafeComment> cafeCommentList = commentRepository.findAllByCafeIdOrderByregDateDesc(cafeId);
//        List<CafeCommentResponseDto> responseDtoList = new ArrayList<>();
//
//        cafeCommentList.forEach(comment ->
//                responseDtoList.add(new CafeCommentResponseDto(
//                        comment, userDetails, commentLikeReposiotory
//                        )));
//        return responseDtoList;
//    }

    //레시피에 대한 댓글들 조회- 페이지로 리턴
    public Page<CafeCommentResponseDto> getCommentByPage(Long cafeId, int page, int size, boolean isAsc, UserDetailsImpl userDetails){

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "regDate");
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<CafeComment> comments = commentRepository.findAllByCafeId(cafeId,pageable);

        //필요한 정보만 리턴
        Page<CafeCommentResponseDto> responseDtos = comments.map((comment -> new CafeCommentResponseDto(comment,userDetails,commentLikeReposiotory)));
        return responseDtos;
    }


    //댓글 삭제
    public void deleteComment(Long commentId, UserDetailsImpl userDetails) {
        commentRepository.deleteById(commentId);
    }

    //댓글 수정
    public CafeCommentResponseDto updateComment(Long commentId, CafeCommentUpdateRequestDto updateRequestDto, UserDetailsImpl userDetails) {
        CafeComment cafeComment = commentRepository.findById(commentId).orElseThrow(()->
                new CustomErrorException("해당 댓글이 존재하지 않습니다"));
        CafeComment savedComment = cafeComment.updateComment(updateRequestDto.getContent());
        CafeCommentResponseDto responseDto = new CafeCommentResponseDto(savedComment, userDetails, commentLikeReposiotory);
        return responseDto;
    }

    public Optional<CafeComment> findById(Long cafeId) {
        return commentRepository.findById(cafeId);
    }

    public String likeComment(Long commentId, User user) {
        CafeComment comment = commentRepository.findById(commentId).orElseThrow(()->
                new CustomErrorException("해당 게시물이 존재하지 않아요"));
        //이미 좋아요 누른건지 확인하기
        Optional<CafeCommentLike> foundCommentLikes = commentLikeReposiotory.findByCafeCommentAndUser(comment, user);
        if(foundCommentLikes.isPresent()){
            //이미 좋아요를 눌렀으면 좋아요취소
            commentLikeReposiotory.delete(foundCommentLikes.get());
            return "좋아요 취소 성공";
        }else{
            CafeCommentLike commentLikes = new CafeCommentLike(user, comment);
            commentLikeReposiotory.save(commentLikes);
            return "좋아요 등록 성공";
        }
    }
}
