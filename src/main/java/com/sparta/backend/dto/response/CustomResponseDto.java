package com.sparta.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CustomResponseDto<T> {
    private int code;
    private String message;
    private T data;
}