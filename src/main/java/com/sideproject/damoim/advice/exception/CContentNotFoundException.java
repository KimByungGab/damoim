package com.sideproject.damoim.advice.exception;

public class CContentNotFoundException extends RuntimeException{
    public CContentNotFoundException() {
        super();
    }

    public CContentNotFoundException(String message) {
        super(message);
    }

    public CContentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
