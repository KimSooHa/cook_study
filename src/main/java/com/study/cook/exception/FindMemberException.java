package com.study.cook.exception;

public class FindMemberException extends IllegalArgumentException {


    public FindMemberException(String message) {
        super(message);
    }

    public FindMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public FindMemberException(Throwable cause) {
        super(cause);
    }
}
