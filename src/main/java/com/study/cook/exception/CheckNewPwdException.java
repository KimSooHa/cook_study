package com.study.cook.exception;

public class CheckNewPwdException extends IllegalArgumentException {


    public CheckNewPwdException(String message) {
        super(message);
    }

    public CheckNewPwdException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckNewPwdException(Throwable cause) {
        super(cause);
    }
}
