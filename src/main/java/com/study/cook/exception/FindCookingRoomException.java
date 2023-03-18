package com.study.cook.exception;

import java.util.NoSuchElementException;

public class FindCookingRoomException extends NoSuchElementException {

    public FindCookingRoomException(String message) {
        super(message);
    }

}
