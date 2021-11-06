package com.sparta.backend.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserInfoResponseDto {

    private String token;
    private String nickname;
    private String image;

}
