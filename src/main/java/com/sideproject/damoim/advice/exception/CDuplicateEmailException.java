package com.sideproject.damoim.advice.exception;

public class CDuplicateEmailException extends RuntimeException{
    public CDuplicateEmailException() {
        super();
    }

    public CDuplicateEmailException(String message) {
        super(message);
    }

    public CDuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
