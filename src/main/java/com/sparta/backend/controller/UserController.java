package com.sparta.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.backend.dto.request.user.*;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.user.GetUserInfoResponseDto;
import com.sparta.backend.dto.response.user.UserInfoResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.KakaoUserService;
import com.sparta.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final KakaoUserService kakaoUserService;

    // 회원가입 요청
    @PostMapping("/user/signup")
    public CustomResponseDto<?> registerUser(@Valid @RequestBody SignupRequestDto requestDto, Errors errors) {

        if(errors.hasErrors()){
            return new CustomResponseDto<>(-1, "회원가입 실패", errors.getAllErrors());
        }

        userService.registerUser(requestDto);
        return new CustomResponseDto<>(1, "회원가입 성공", "");
    }

    // 이메일 중복 체크
    @PostMapping("/user/signup/email")
    public CustomResponseDto<?> validCheckEmail(@RequestBody ValidEmailRequestDto requestDto) {

        int result = userService.validCheckEmail(requestDto.getEmail());

        if (result == 0) {
            return new CustomResponseDto<>(1, "사용할 수 있는 이메일입니다", "");
        } else if (result == 1) {
            return new CustomResponseDto<>(-1, "이미 존재하는 이메일입니다", "");
        } else {
            return new CustomResponseDto<>(-1, "이메일 형식이 아닙니다", "");
        }
    }

    // 닉네임 중복 체크
    @PostMapping("/user/signup/nickname")
    public CustomResponseDto<?> validCheckNickname(@RequestBody ValidNicknameRequestDto requestDto) {

        log.info("nickname = {}", requestDto.getNickname());

        int result = userService.validCheckNickname(requestDto.getNickname());

        if (result == 0) {
            return new CustomResponseDto<>(1, "사용할 수 있는 닉네임입니다", "");
        } else if (result == 1) {
            return new CustomResponseDto<>(-1, "이미 존재하는 닉네임입니다", "");
        } else {
            return new CustomResponseDto<>(-1, "잘못된 닉네임 형식입니다", "");
        }
    }


    // 로그인 요청
    @PostMapping("/user/login")
    public CustomResponseDto<?> login(@RequestBody SignupRequestDto requestDto) {

        GetUserInfoResponseDto responseDto = userService.login(requestDto);

        return new CustomResponseDto<>(1, "로그인 성공", responseDto);
    }

    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public CustomResponseDto<?> kakaoLogin(@RequestParam String code) throws JsonProcessingException {

        kakaoUserService.kakaoLogin(code);

        return new CustomResponseDto<>(1, "로그인 성공", "");
    }

    // 회원 정보 조회
    @GetMapping("/user/info")
    public CustomResponseDto<?> userInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        UserInfoResponseDto responseDto = new UserInfoResponseDto(userDetails.getUser().getNickname(), userDetails.getUser().getImage());

        return new CustomResponseDto<>(1, "회원 정보 조회 성공", responseDto);
    }

    // 회원 정보 수정
    @PutMapping("/user/info")
    public CustomResponseDto<?> updateUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UpdateUserRequestDto requestDto) throws IOException {

        Long userId = userDetails.getUser().getId();

        userService.updateUser(userId, requestDto);

        return new CustomResponseDto<>(1, "회원 정보 수정 성공", "");
    }

    // 회원 탈퇴
    @PutMapping("/user/delete")
    public CustomResponseDto<?> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody DeleteUserRequestDto requestDto) {

        Long userId = userDetails.getUser().getId();

        userService.deleteUser(userId, requestDto);

        return new CustomResponseDto<>(1, "회원 탈퇴 성공", "");
    }

}
