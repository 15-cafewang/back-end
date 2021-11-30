package com.sparta.backend.exception;

public class ImageNameTooLongException extends RuntimeException{
    public ImageNameTooLongException(String msg) {
        super(msg);
    }
}
