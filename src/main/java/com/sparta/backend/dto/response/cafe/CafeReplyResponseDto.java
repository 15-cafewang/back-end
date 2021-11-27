package com.sparta.backend.dto.response.cafe;

import com.sparta.backend.domain.cafe.*;
import com.sparta.backend.repository.cafe.CafeCommentLikeRepository;
import com.sparta.backend.repository.cafe.CafeReplyLikeRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@AllArgsConstructor
public class CafeReplyResponseDto {
    private Long replyId;
    private Long commentId;
    private String nickname;
    private String reply;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private int likeCount;
    private boolean likeStatus;
    private String profile;

    public CafeReplyResponseDto(CafeReply cafeReply, UserDetailsImpl userDetails, CafeReplyLikeRepository commentLikeRepository){
        this.replyId = cafeReply.getId();
        this.commentId = cafeReply.getCafeComment().getId();
        this.nickname = cafeReply.getUser().getNickname();
        this.reply = cafeReply.getReply();
        this.regDate = cafeReply.getRegDate();
        this.modDate = cafeReply.getModDate();
        this.likeCount = cafeReply.getReplyLikes() == null? 0: cafeReply.getReplyLikes().size();

        Optional<CafeReplyLike> foundCommentLikes = commentLikeRepository.findByCafeReplyAndUser(cafeReply,userDetails.getUser());
        this.likeStatus = foundCommentLikes.isPresent();
        profile = cafeReply.getUser().getImage();
    }
}
