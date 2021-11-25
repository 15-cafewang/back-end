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
public class GetRecipeListResponseDto {

    private Long recipeId;
    private String title;
    private String nickname;
    private String location;
    private List<String> imageList = new ArrayList<>();
    private int likeCount;
    private boolean likeStatus;

    public GetRecipeListResponseDto(Cafe cafe,
                                    UserDetailsImpl userDetails,
                                    CafeLikeRepository cafeLikeRepository) {

        this.recipeId = cafe.getId();
        this.title = cafe.getTitle();
        this.nickname = cafe.getUser().getNickname();
        this.location = cafe.getLocation();
        cafe.getCafeImagesList().forEach(RecipeImage -> this.imageList.add(RecipeImage.getImage()));
        this.likeCount = cafe.getCafeLikeList().size();

        Optional<CafeLike> foundRecipeLike = cafeLikeRepository
                .findByCafeIdAndUserId(cafe.getId(), userDetails.getUser().getId());
        this.likeStatus = foundRecipeLike.isPresent();
    }

}
