package com.sparta.backend.dto.response.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetMypageResponseDto {

    private String image;        // 프사
    private String nickname;     // 닉네임
    private int followCount;     // 팔로워 수
    private int folloingCount;   // 팔로잉 수
    private String followStatus; // 팔로우 상태

}
