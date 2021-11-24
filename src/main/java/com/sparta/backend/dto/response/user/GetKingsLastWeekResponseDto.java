package com.sparta.backend.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetKingsLastWeekResponseDto {
    private GetKingUserInfoLastWeekResponseDto getLikeKing;
    private GetKingUserInfoLastWeekResponseDto geFollowKing;
    private GetKingUserInfoLastWeekResponseDto gePostKing;
    private GetKingUserInfoLastWeekResponseDto getCommentKing;
}
