package com.sparta.backend.controller;

import com.sparta.backend.dto.request.user.SignupRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    // 회원가입 요청
    @PostMapping("/user/signup")
    public CustomResponseDto<?> registerUser(@Valid @RequestBody SignupRequestDto requestDto, Errors errors) {

        if(errors.hasErrors()){
            return new CustomResponseDto<>(-1, "회원가입 실패", errors.getAllErrors());
        }

        userService.registerUser(requestDto);
        return new CustomResponseDto<>(1, "회원가입 성공", "");
    }

    // 로그인 요청
    @PostMapping("/user/login")
    public CustomResponseDto<?> login(@RequestBody SignupRequestDto requestDto) {

        List<Map<String, String>> ret = userService.login(requestDto);

        return new CustomResponseDto<>(1, "로그인 성공", ret);
    }

}
