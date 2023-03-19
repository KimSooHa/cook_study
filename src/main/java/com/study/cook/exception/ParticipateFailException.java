package com.study.cook.exception;

public class ParticipateFailException extends RuntimeException {


    public ParticipateFailException(String message) {
        super(message);
    }

    public ParticipateFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParticipateFailException(Throwable cause) {
        super(cause);
    }
}
