package com.study.cook.exception;

public class FindRecipeException extends IllegalArgumentException {


    public FindRecipeException(String message) {
        super(message);
    }

    public FindRecipeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FindRecipeException(Throwable cause) {
        super(cause);
    }
}
