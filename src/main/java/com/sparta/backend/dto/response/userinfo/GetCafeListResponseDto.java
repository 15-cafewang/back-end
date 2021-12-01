package com.sparta.backend.dto.response.userinfo;

import com.sparta.backend.domain.cafe.Cafe;
import com.sparta.backend.domain.cafe.CafeLike;
import com.sparta.backend.repository.cafe.CafeLikeRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetCafeListResponseDto {

    private Long cafeId;
    private String title;
    private String nickname;
    private String location;
    private String image;
    private int likeCount;
    private boolean likeStatus;
    private int commentCount;
    private int rankingStatus;

    public GetCafeListResponseDto(Cafe cafe,
                                  UserDetailsImpl userDetails,
                                  CafeLikeRepository cafeLikeRepository) {

        this.cafeId = cafe.getId();
        this.title = cafe.getTitle();
        this.nickname = cafe.getUser().getNickname();
        this.location = cafe.getLocation();
        this.image = getThumbNail(cafe);
        this.likeCount = cafe.getCafeLikeList().size();
        this.commentCount = cafe.getCafeCommentList().size();

        Optional<CafeLike> foundRecipeLike = cafeLikeRepository
                .findByCafeIdAndUserId(cafe.getId(), userDetails.getUser().getId());
        this.likeStatus = foundRecipeLike.isPresent();
        this.rankingStatus = cafe.getUser().getRankingStatus();
    }

    public String getThumbNail(Cafe cafe) {
        if(cafe.getThumbNailImage() == null) return cafe.getCafeImagesList().get(0).getImage();
        else return cafe.getThumbNailImage();
    }

}
