package com.sparta.backend.dto.response.cafe;

import com.sparta.backend.domain.user.User;
import com.sparta.backend.domain.cafe.Cafe;
import com.sparta.backend.domain.cafe.CafeLike;
import com.sparta.backend.repository.cafe.CafeLikeRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class CafeRecommendResponseDto {
    private Long cafeId;
    private String nickname;
    private String title;
    private String content;
    private List<String> images = new ArrayList<>();
    private LocalDateTime regDate;
    private int commentCount;
    private int likeCount;
    private String location;
    private boolean likeStatus;
    private String tagName;


    public CafeRecommendResponseDto(Cafe cafe, UserDetailsImpl userDetails, CafeLikeRepository cafeLikeRepository){
        this.cafeId = cafe.getId();
        this.nickname = cafe.getUser().getNickname();
        this.title = cafe.getTitle();
        this.content = cafe.getContent();
        this.regDate = cafe.getRegDate();
        this.commentCount = cafe.getCafeCommentList().size();
        cafe.getCafeImagesList().forEach((CafeImage)->this.images.add(CafeImage.getImage()));
        this.likeCount = cafe.getCafeLikeList().size();
        this.location = cafe.getLocation();

        Optional<CafeLike> foundCafeLike = cafeLikeRepository.findByCafeIdAndUserId(cafe.getId(),userDetails.getUser().getId());
        this.likeStatus = foundCafeLike.isPresent();
    }
//
    public CafeRecommendResponseDto(Cafe cafe, String tagName, User user, CafeLikeRepository cafeLikeRepository){
        this.cafeId = cafe.getId();
        this.nickname = cafe.getUser().getNickname(); //todo:N+1 해결하면 될듯
        this.title = cafe.getTitle();
        this.content = cafe.getContent();
        this.regDate = cafe.getRegDate();
        this.commentCount = cafe.getCafeCommentList().size();
        cafe.getCafeImagesList().forEach((CafeImage)->this.images.add(CafeImage.getImage()));
        this.likeCount = cafe.getCafeLikeList().size();
        this.location = cafe.getLocation();

        Optional<CafeLike> foundCafeLike = cafeLikeRepository.findByCafeAndUser(cafe,user);
        this.likeStatus = foundCafeLike.isPresent();
        this.tagName = tagName;
    }

    public CafeRecommendResponseDto(Optional<Cafe> cafe, User user, CafeLikeRepository likesRepository) {
        this.cafeId = cafe.get().getId();
        this.nickname = cafe.get().getUser().getNickname();
        this.title = cafe.get().getTitle();
        this.content = cafe.get().getContent();
        this.regDate = cafe.get().getRegDate();
        this.commentCount = cafe.get().getCafeCommentList().size();
        cafe.get().getCafeImagesList().forEach((CafeImage)->this.images.add(CafeImage.getImage()));
        this.likeCount = cafe.get().getCafeLikeList().size();
        this.location = cafe.get().getLocation();

        Optional<CafeLike> foundCafeLike = likesRepository.findByCafeIdAndUserId(cafe.get().getId(), user.getId());
        this.likeStatus = foundCafeLike.isPresent();
    }
}