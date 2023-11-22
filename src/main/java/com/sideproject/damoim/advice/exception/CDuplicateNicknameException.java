package com.sideproject.damoim.advice.exception;

public class CDuplicateNicknameException extends RuntimeException{
    public CDuplicateNicknameException() {
        super();
    }

    public CDuplicateNicknameException(String message) {
        super(message);
    }

    public CDuplicateNicknameException(String message, Throwable cause) {
        super(message, cause);
    }
}
