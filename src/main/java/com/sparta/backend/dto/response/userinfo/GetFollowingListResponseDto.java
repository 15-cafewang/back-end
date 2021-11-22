package com.sparta.backend.dto.response.userinfo;

import com.sparta.backend.domain.user.Follow;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetFollowingListResponseDto {

    String nickname;
    String image;

    public GetFollowingListResponseDto(Follow follow) {
        this.nickname = follow.getToUser().getNickname();
        this.image = follow.getToUser().getImage();
    }
}
