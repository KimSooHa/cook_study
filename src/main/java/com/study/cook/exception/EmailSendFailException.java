package com.study.cook.exception;

public class EmailSendFailException extends RuntimeException {
    public EmailSendFailException(String message) {
        super(message);
    }
}