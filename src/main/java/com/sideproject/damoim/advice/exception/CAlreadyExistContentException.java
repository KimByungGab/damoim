package com.sideproject.damoim.advice.exception;

public class CAlreadyExistContentException extends RuntimeException{
    public CAlreadyExistContentException() {
        super();
    }

    public CAlreadyExistContentException(String message) {
        super(message);
    }

    public CAlreadyExistContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
