package com.sideproject.damoim.advice.exception;

public class CAlreadyProcessedContentException extends RuntimeException{
    public CAlreadyProcessedContentException() {
        super();
    }

    public CAlreadyProcessedContentException(String message) {
        super(message);
    }

    public CAlreadyProcessedContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
