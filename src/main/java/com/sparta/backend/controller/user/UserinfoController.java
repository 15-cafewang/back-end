package com.sparta.backend.controller.user;

import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.userinfo.*;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.user.UserinfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserinfoController {

    private final UserinfoService userinfoService;

    // 마이페이지 조회
    @GetMapping("/userinfo/{nickname}")
    public CustomResponseDto<?> getUserInfo(@PathVariable String nickname,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        GetUserinfoResponseDto responseDto = userinfoService.getUserInfo(userDetails, nickname);

        return new CustomResponseDto<>(1, "마이페이지 조회 성공", responseDto);
    }

    // 내가 쓴 레시피 목록 조회
    @GetMapping("/userinfo/recipes/{nickname}")
    public CustomResponseDto<?> getRecipeList(@RequestParam("page") int page,
                                              @RequestParam("size") int size,
                                              @RequestParam("isAsc") boolean isAsc,
                                              @RequestParam("sortBy") String sortBy,
                                              @PathVariable String nickname,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        Page<GetRecipeListResponseDto> recipeList = userinfoService
                .getRecipeListByPage(page, size, isAsc, sortBy, nickname, userDetails);

        return new CustomResponseDto<>(1, "레시피 목록 조회 성공", recipeList);
    }

    // 내가 쓴 게시글 목록 조회
    @GetMapping("/userinfo/boards/{nickname}")
    public CustomResponseDto<?> getBoardList(@RequestParam("page") int page,
                                             @RequestParam("size") int size,
                                             @RequestParam("isAsc") boolean isAsc,
                                             @RequestParam("sortBy") String sortBy,
                                             @PathVariable String nickname,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        Page<GetBoardListResponseDto> boardList = userinfoService
                .getBoardListByPage(page, size, isAsc, sortBy, nickname, userDetails);

        return new CustomResponseDto<>(1, "게시글 목록 조회 성공", boardList);
    }

    // 내가 좋아요한 레시피 목록 조회
    @GetMapping("/userinfo/recipes/likes/{nickname}")
    public CustomResponseDto<?> getLikedRecipeList(@RequestParam("page") int page,
                                                   @RequestParam("size") int size,
                                                   @RequestParam("isAsc") boolean isAsc,
                                                   @RequestParam("sortBy") String sortBy,
                                                   @PathVariable String nickname,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        Page<GetRecipeListResponseDto> likedRecipeList = userinfoService
                .getLikedRecipeListByPage(page, size, isAsc, sortBy, nickname, userDetails);

        return new CustomResponseDto<>(1, "좋아요한 레시피 목록 조회 성공", likedRecipeList);
    }

    // 내가 좋아요한 게시글 목록 조회
    @GetMapping("/userinfo/boards/likes/{nickname}")
    public CustomResponseDto<?> getLikedBoardList(@RequestParam("page") int page,
                                                  @RequestParam("size") int size,
                                                  @RequestParam("isAsc") boolean isAsc,
                                                  @RequestParam("sortBy") String sortBy,
                                                  @PathVariable String nickname,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        Page<GetBoardListResponseDto> likedBoardList = userinfoService
                .getLikedBoardListByPage(page, size, isAsc, sortBy, nickname, userDetails);

        return new CustomResponseDto<>(1, "좋아요한 게시글 목록 조회 성공", likedBoardList);
    }

    // 팔로잉 목록 조회
    @GetMapping("/userinfo/follows/following/{nickname}")
    public CustomResponseDto<?> getFollowingList(@RequestParam("page") int page,
                                                 @RequestParam("size") int size,
                                                 @RequestParam("isAsc") boolean isAsc,
                                                 @RequestParam("sortBy") String sortBy,
                                                 @PathVariable String nickname,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        Page<GetFollowingListResponseDto> followingList = userinfoService
                .getFollowingListByPage(page, size, isAsc, sortBy, nickname, userDetails);

        return new CustomResponseDto<>(1, "팔로잉 목록 조회 성공", followingList);
    }

    // 팔로워 목록 조회
    @GetMapping("/userinfo/follows/follower/{nickname}")
    public CustomResponseDto<?> getFollowerList(@RequestParam("page") int page,
                                                @RequestParam("size") int size,
                                                @RequestParam("isAsc") boolean isAsc,
                                                @RequestParam("sortBy") String sortBy,
                                                @PathVariable String nickname,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        Page<GetFollowerListResponseDto> followingList = userinfoService
                .getFollowerListByPage(page, size, isAsc, sortBy, nickname, userDetails);

        return new CustomResponseDto<>(1, "팔로워 목록 조회 성공", followingList);
    }

    private void checkLogin(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails == null) {
            throw new CustomErrorException("로그인된 유저만 사용가능한 기능입니다.");
        }
    }
}
