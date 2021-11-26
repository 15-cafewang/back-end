package com.sparta.backend.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetThisWeekRankingResponseDto {

    private String nickname;
    private String image;
    private Long count;
}
