package com.sparta.backend.dto.response.userinfo;

import com.sparta.backend.domain.Follow;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetFollowerListResponseDto {

    String nickname;
    String image;

    public GetFollowerListResponseDto(Follow follow) {
        this.nickname = follow.getFromUser().getNickname();
        this.image = follow.getFromUser().getImage();
    }
}
