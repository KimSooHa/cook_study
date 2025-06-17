package com.study.cook.exception;

public class InvalidClubCapacityException extends IllegalArgumentException {


    public InvalidClubCapacityException(String message) {
        super(message);
    }

    public InvalidClubCapacityException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidClubCapacityException(Throwable cause) {
        super(cause);
    }
}
