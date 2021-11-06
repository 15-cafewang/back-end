package com.sparta.backend.service;

import com.sparta.backend.dto.response.userinfo.GetUserinfoResponseDto;
import com.sparta.backend.security.UserDetailsImpl;

public interface UserinfoService {

    // 마이페이지 조회(프사, 닉네임, 팔로워 수, 팔로잉 수)
    GetUserinfoResponseDto getUserInfo(UserDetailsImpl userDetails, String nickname);

    // 내가 쓴 레시피 목록 조회
    void getRecipeList();

    // 내가 쓴 게시글 목록 조회
    void boardList();

    // 내가 좋아요한 레시피 목록 조회
    void likedRecipeList();

    // 내가 좋아요한 게시글 목록 조회
    void likedBoardList();
}
