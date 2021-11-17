package com.sparta.backend.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^((?=.*[0-9])(?=.*[a-zA-Z])(?=.*[\\W]).{8,20})$",
            message = "비밀번호는 영문 대,소문자와 숫자, 8자 ~ 20자의 비밀번호여야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 중복검사는 필수 입니다.")
    private String passwordCheck;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Pattern(regexp = "^([0-9a-zA-Z가-힣]{2,8})$",
            message = "닉네임은 특수문자를 포함하지 않은 2자 이상 8자 이하여야합니다.")
    private String nickname;
}
