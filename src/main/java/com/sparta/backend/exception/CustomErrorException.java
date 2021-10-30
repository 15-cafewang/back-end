package com.sparta.backend.exception;

public class CustomErrorException extends RuntimeException{
    public CustomErrorException(String msg) {
        super(msg);
    }
}
