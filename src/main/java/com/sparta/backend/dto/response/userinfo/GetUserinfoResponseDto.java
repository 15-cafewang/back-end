package com.sparta.backend.dto.response.userinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserinfoResponseDto {

    private String image;        // 프사
    private String nickname;     // 닉네임
    private int followCount;     // 팔로워 수
    private int followingCount;   // 팔로잉 수
    private boolean followStatus; // 팔로우 상태

}
