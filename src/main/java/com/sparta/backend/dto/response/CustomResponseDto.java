package com.sparta.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CustomResponseDto<T> {
    private int code;
    private String message;
    private T data;
}
