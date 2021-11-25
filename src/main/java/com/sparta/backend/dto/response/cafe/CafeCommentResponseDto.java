package com.sparta.backend.dto.response.cafe;

import com.sparta.backend.domain.cafe.CafeComment;
import com.sparta.backend.domain.cafe.CafeCommentLike;
import com.sparta.backend.repository.cafe.CafeCommentLikeRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@AllArgsConstructor
public class CafeCommentResponseDto {
    private Long commentId;
    private Long cafeId;
    private String nickname;
    private String content;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private int likeCount;
    private boolean likeStatus;
    private String profile;

    public CafeCommentResponseDto(CafeComment cafeComment, UserDetailsImpl userDetails, CafeCommentLikeRepository commentLikeRepository){
        this.commentId = cafeComment.getId();
        this.cafeId = cafeComment.getCafe().getId();
        this.nickname = cafeComment.getUser().getNickname();
        this.content = cafeComment.getContent();
        this.regDate = cafeComment.getRegDate();
        this.modDate = cafeComment.getModDate();
        this.likeCount = cafeComment.getCommentLikes() == null? 0: cafeComment.getCommentLikes().size();

        Optional<CafeCommentLike> foundCommentLikes = commentLikeRepository.findByCafeCommentAndUser(cafeComment,userDetails.getUser());
        this.likeStatus = foundCommentLikes.isPresent();
        profile = cafeComment.getUser().getImage();
    }
}
