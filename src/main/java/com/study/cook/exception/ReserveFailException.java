package com.study.cook.exception;

public class ReserveFailException extends RuntimeException {


    public ReserveFailException(String message) {
        super(message);
    }

    public ReserveFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReserveFailException(Throwable cause) {
        super(cause);
    }
}
