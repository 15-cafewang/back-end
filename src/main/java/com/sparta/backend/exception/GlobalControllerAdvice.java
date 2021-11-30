package com.sparta.backend.exception;

import com.sparta.backend.dto.response.CustomResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> nullExHandle(NullPointerException e) {

        return new ResponseEntity<>(new CustomResponseDto<>(-1, e.getMessage(),""), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> illegalExHandle(IllegalArgumentException e) {

        return new ResponseEntity<>(new CustomResponseDto<>(-1, e.getMessage(),""), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> noSuchElemHandle(NoSuchElementException e) {

        return new ResponseEntity<>(new CustomResponseDto<>(-1, e.getMessage(),""), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomErrorException.class)
    public ResponseEntity<?> noSuchElemHandle(CustomErrorException e) {

        return new ResponseEntity<>(new CustomResponseDto<>(-1, e.getMessage(),""), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ImageNameTooLongException.class)
    public ResponseEntity<?> imageNameTooLongException(ImageNameTooLongException e) {

        return new ResponseEntity<>(new CustomResponseDto<>(-1, e.getMessage(),""), HttpStatus.BAD_REQUEST);
    }
}