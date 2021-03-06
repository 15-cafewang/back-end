package com.sparta.backend.controller.user;

import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.userinfo.*;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.user.UserinfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getUserInfo(@PathVariable String nickname,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        GetUserinfoResponseDto responseDto = userinfoService.getUserInfo(userDetails, nickname);

        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "마이페이지 조회 성공", responseDto), HttpStatus.OK);
    }

    // 내가 쓴 카페 목록 조회
    @GetMapping("/userinfo/cafes/{nickname}")
    public ResponseEntity<?> getRecipeList(@RequestParam("page") int page,
                                           @RequestParam("size") int size,
                                           @RequestParam("isAsc") boolean isAsc,
                                           @RequestParam("sortBy") String sortBy,
                                           @PathVariable String nickname,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        Page<GetCafeListResponseDto> cafeList = userinfoService
                .getRecipeListByPage(page, size, isAsc, sortBy, nickname, userDetails);

        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "카페 목록 조회 성공", cafeList), HttpStatus.OK);
    }

    // 내가 쓴 게시글 목록 조회
    @GetMapping("/userinfo/boards/{nickname}")
    public ResponseEntity<?> getBoardList(@RequestParam("page") int page,
                                          @RequestParam("size") int size,
                                          @RequestParam("isAsc") boolean isAsc,
                                          @RequestParam("sortBy") String sortBy,
                                          @PathVariable String nickname,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        Page<GetBoardListResponseDto> boardList = userinfoService
                .getBoardListByPage(page, size, isAsc, sortBy, nickname, userDetails);

        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "게시글 목록 조회 성공", boardList), HttpStatus.OK);
    }

    // 내가 좋아요한 카페 목록 조회
    @GetMapping("/userinfo/cafes/likes/{nickname}")
    public ResponseEntity<?> getLikedRecipeList(@RequestParam("page") int page,
                                               @RequestParam("size") int size,
                                               @RequestParam("isAsc") boolean isAsc,
                                               @RequestParam("sortBy") String sortBy,
                                               @PathVariable String nickname,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        Page<GetCafeListResponseDto> likedCafeList = userinfoService
                .getLikedRecipeListByPage(page, size, isAsc, sortBy, nickname, userDetails);

        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "좋아요한 카페 목록 조회 성공", likedCafeList), HttpStatus.OK);
    }

    // 내가 좋아요한 게시글 목록 조회
    @GetMapping("/userinfo/boards/likes/{nickname}")
    public ResponseEntity<?> getLikedBoardList(@RequestParam("page") int page,
                                               @RequestParam("size") int size,
                                               @RequestParam("isAsc") boolean isAsc,
                                               @RequestParam("sortBy") String sortBy,
                                               @PathVariable String nickname,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        Page<GetBoardListResponseDto> likedBoardList = userinfoService
                .getLikedBoardListByPage(page, size, isAsc, sortBy, nickname, userDetails);

        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "좋아요한 게시글 목록 조회 성공", likedBoardList), HttpStatus.OK);
    }

    // 팔로잉 목록 조회
    @GetMapping("/userinfo/follows/following/{nickname}")
    public ResponseEntity<?> getFollowingList(@RequestParam("page") int page,
                                              @RequestParam("size") int size,
                                              @RequestParam("isAsc") boolean isAsc,
                                              @RequestParam("sortBy") String sortBy,
                                              @PathVariable String nickname,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        Page<GetFollowingListResponseDto> followingList = userinfoService
                .getFollowingListByPage(page, size, isAsc, sortBy, nickname, userDetails);

        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "팔로잉 목록 조회 성공", followingList), HttpStatus.OK);
    }

    // 팔로워 목록 조회
    @GetMapping("/userinfo/follows/follower/{nickname}")
    public ResponseEntity<?> getFollowerList(@RequestParam("page") int page,
                                             @RequestParam("size") int size,
                                             @RequestParam("isAsc") boolean isAsc,
                                             @RequestParam("sortBy") String sortBy,
                                             @PathVariable String nickname,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        Page<GetFollowerListResponseDto> followingList = userinfoService
                .getFollowerListByPage(page, size, isAsc, sortBy, nickname, userDetails);

        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "팔로워 목록 조회 성공", followingList), HttpStatus.OK);
    }

    private void checkLogin(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails == null) {
            throw new CustomErrorException("로그인된 유저만 사용가능한 기능입니다.");
        }
    }
}
