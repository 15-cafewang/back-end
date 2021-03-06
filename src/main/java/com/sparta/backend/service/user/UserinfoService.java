package com.sparta.backend.service.user;

import com.sparta.backend.dto.response.userinfo.*;
import com.sparta.backend.security.UserDetailsImpl;
import org.springframework.data.domain.Page;

public interface UserinfoService {

    // 마이페이지 조회(프사, 닉네임, 팔로워 수, 팔로잉 수)
    GetUserinfoResponseDto getUserInfo(UserDetailsImpl userDetails, String nickname);

    // 내가 쓴 카페 목록 조회
    Page<GetCafeListResponseDto> getRecipeListByPage(int page,
                                                     int size,
                                                     boolean isAsc,
                                                     String sortBy,
                                                     String nickname,
                                                     UserDetailsImpl userDetails);

    // 내가 쓴 게시글 목록 조회
    Page<GetBoardListResponseDto> getBoardListByPage(int page,
                                                     int size,
                                                     boolean isAsc,
                                                     String sortBy,
                                                     String nickname,
                                                     UserDetailsImpl userDetails);

    // 내가 좋아요한 카페 목록 조회
    Page<GetCafeListResponseDto> getLikedRecipeListByPage(int page,
                                                          int size,
                                                          boolean isAsc,
                                                          String sortBy,
                                                          String nickname,
                                                          UserDetailsImpl userDetails);

    // 내가 좋아요한 게시글 목록 조회
    Page<GetBoardListResponseDto> getLikedBoardListByPage(int page,
                                                          int size,
                                                          boolean isAsc,
                                                          String sortBy,
                                                          String nickname,
                                                          UserDetailsImpl userDetails);

    // 팔로잉 목록 조회
    Page<GetFollowingListResponseDto> getFollowingListByPage(int page,
                                                             int size,
                                                             boolean isAsc,
                                                             String sortBy,
                                                             String nickname,
                                                             UserDetailsImpl userDetails);

    // 팔로워 목록 조회
    Page<GetFollowerListResponseDto> getFollowerListByPage(int page,
                                                           int size,
                                                           boolean isAsc,
                                                           String sortBy,
                                                           String nickname,
                                                           UserDetailsImpl userDetails);

}
