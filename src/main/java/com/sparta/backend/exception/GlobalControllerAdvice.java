package com.sparta.backend.exception;

import com.sparta.backend.dto.response.CustomResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullPointerException.class)
    public CustomResponseDto<?> nullExHandle(NullPointerException e) {

        return new CustomResponseDto<>(-1, e.getMessage(),"");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public CustomResponseDto<?> illegalExHandle(IllegalArgumentException e) {

        return new CustomResponseDto<>(-1, e.getMessage(),"");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchElementException.class)
    public CustomResponseDto<?> noSuchElemHandle(NoSuchElementException e) {

        return new CustomResponseDto<>(-1, e.getMessage(),"");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomErrorException.class)
    public CustomResponseDto<?> noSuchElemHandle(CustomErrorException e) {

        return new CustomResponseDto<>(-1, e.getMessage(),"");
    }
}