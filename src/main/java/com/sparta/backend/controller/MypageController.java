package com.sparta.backend.controller;

import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.mypage.GetMypageResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/mypage/{nickname}")
    public CustomResponseDto<?> getMypageInfo(@PathVariable String nickname, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        GetMypageResponseDto responseDto = mypageService.getMypageInfo(userDetails, nickname);

        return new CustomResponseDto<>(1, "마이페이지 조회 성공", responseDto);
    }
}
