package com.study.cook.exception;

import java.util.NoSuchElementException;

public class FindReservationException extends NoSuchElementException {

    public FindReservationException(String message) {
        super(message);
    }

}
