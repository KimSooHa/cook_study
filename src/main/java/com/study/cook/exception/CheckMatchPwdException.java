package com.study.cook.exception;

public class CheckMatchPwdException extends IllegalArgumentException {


    public CheckMatchPwdException(String message) {
        super(message);
    }

    public CheckMatchPwdException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckMatchPwdException(Throwable cause) {
        super(cause);
    }
}
