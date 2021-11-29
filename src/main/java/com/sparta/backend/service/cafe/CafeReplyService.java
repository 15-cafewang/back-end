package com.sparta.backend.service.cafe;

import com.sparta.backend.domain.cafe.Cafe;
import com.sparta.backend.domain.cafe.CafeComment;
import com.sparta.backend.domain.cafe.CafeReply;
import com.sparta.backend.dto.request.cafe.CafePostReplyRequestDto;
import com.sparta.backend.dto.response.cafe.CafeReplyResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.repository.cafe.CafeCommentLikeRepository;
import com.sparta.backend.repository.cafe.CafeCommentRepository;
import com.sparta.backend.repository.cafe.CafeReplyLikeRepository;
import com.sparta.backend.repository.cafe.CafeReplyRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CafeReplyService {
    private final CafeCommentRepository commentRepository;
    private final CafeReplyRepository replyRepository;
    private final CafeReplyLikeRepository replyLikeRepository;

    public CafeReplyResponseDto saveReply(CafePostReplyRequestDto requestDto, UserDetailsImpl userDetails) {
        CafeComment comment = commentRepository.findById(requestDto.getCommentId()).orElseThrow(()->
                new CustomErrorException("해당 댓글의 카페가 존재하지 않습니다."));

        CafeReply reply = new CafeReply(requestDto.getReply(), userDetails.getUser(), comment);
        CafeReply savedReply = replyRepository.save(reply);

        CafeReplyResponseDto responseDto = new CafeReplyResponseDto(savedReply, userDetails, replyLikeRepository);
        return responseDto;
    }

}
