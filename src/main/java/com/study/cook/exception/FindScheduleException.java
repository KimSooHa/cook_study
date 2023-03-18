package com.study.cook.exception;

import java.util.NoSuchElementException;

public class FindScheduleException extends NoSuchElementException {
    
    public FindScheduleException(String message) {
        super(message);
    }

}
