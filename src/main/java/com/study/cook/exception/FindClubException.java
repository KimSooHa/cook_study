package com.study.cook.exception;

public class FindClubException extends IllegalArgumentException {


    public FindClubException(String message) {
        super(message);
    }

    public FindClubException(String message, Throwable cause) {
        super(message, cause);
    }

    public FindClubException(Throwable cause) {
        super(cause);
    }
}
