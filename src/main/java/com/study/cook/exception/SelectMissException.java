package com.study.cook.exception;

public class SelectMissException extends RuntimeException {


    public SelectMissException(String message) {
        super(message);
    }

    public SelectMissException(String message, Throwable cause) {
        super(message, cause);
    }

    public SelectMissException(Throwable cause) {
        super(cause);
    }
}
