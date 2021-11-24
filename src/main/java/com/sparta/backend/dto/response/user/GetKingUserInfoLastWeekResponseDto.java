package com.sparta.backend.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetKingUserInfoLastWeekResponseDto {
    private Long userId;
    private String nickname;
    private String profile;
    private Integer count;

    public GetKingUserInfoLastWeekResponseDto(Object[] objects) {
        this.userId = ((BigInteger) objects[0]).longValue();
        this.nickname = (String) objects[1];
        this.profile = (String) objects[2];
        this.count = ((BigInteger) objects[3]).intValue();
    }
}
