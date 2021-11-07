package com.sparta.backend.controller;

import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.userinfo.GetBoardListResponseDto;
import com.sparta.backend.dto.response.userinfo.GetRecipeListResponseDto;
import com.sparta.backend.dto.response.userinfo.GetUserinfoResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.UserinfoService;
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

    @GetMapping("/mypage/{nickname}")
    public CustomResponseDto<?> getUserInfo(@PathVariable String nickname, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        GetUserinfoResponseDto responseDto = userinfoService.getUserInfo(userDetails, nickname);

        return new CustomResponseDto<>(1, "마이페이지 조회 성공", responseDto);
    }

    @GetMapping("/mypage/recipes/{nickname}")
    public CustomResponseDto<?> getRecipeList(@RequestParam("page") int page,
                                              @RequestParam("size") int size,
                                              @RequestParam("isAsc") boolean isAsc,
                                              @RequestParam("sortBy") String sortBy,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails,
                                              @PathVariable String nickname) {

        page -= 1;
        Page<GetRecipeListResponseDto> recipeList = userinfoService.getRecipeListByPage(page, size, isAsc, sortBy, userDetails, nickname);

        return new CustomResponseDto<>(1, "레시피 목록 조회 성공", recipeList);
    }

    @GetMapping("/userinfo/boards/{nickname}")
    public CustomResponseDto<?> getBoardList(@RequestParam("page") int page,
                                             @RequestParam("size") int size,
                                             @RequestParam("isAsc") boolean isAsc,
                                             @RequestParam("sortBy") String sortBy,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @PathVariable String nickname) {

        page -= 1;
        Page<GetBoardListResponseDto> boardList = userinfoService.getBoardListByPage(page, size, isAsc, sortBy, userDetails, nickname);

        return new CustomResponseDto<>(1, "게시글 목록 조회 성공", boardList);
    }
}
