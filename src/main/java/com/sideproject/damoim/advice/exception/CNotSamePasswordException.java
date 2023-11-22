package com.sideproject.damoim.advice.exception;

public class CNotSamePasswordException extends RuntimeException{
    public CNotSamePasswordException() {
        super();
    }

    public CNotSamePasswordException(String message) {
        super(message);
    }

    public CNotSamePasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
