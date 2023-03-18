package com.study.cook.exception;

public class FindCommentException extends IllegalArgumentException {


    public FindCommentException(String message) {
        super(message);
    }

    public FindCommentException(String message, Throwable cause) {
        super(message, cause);
    }

    public FindCommentException(Throwable cause) {
        super(cause);
    }
}
