package com.study.cook.exception;

public class EmailAuthLimitException extends RuntimeException {
    public EmailAuthLimitException(String message) {
        super(message);
    }
}