package com.sideproject.damoim.advice.exception;

public class CCanNotProcessYourOwnException extends RuntimeException{
    public CCanNotProcessYourOwnException() {
        super();
    }

    public CCanNotProcessYourOwnException(String message) {
        super(message);
    }

    public CCanNotProcessYourOwnException(String message, Throwable cause) {
        super(message, cause);
    }
}
