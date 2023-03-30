package com.study.cook.exception;

import java.io.IOException;

public class StoreFailException extends RuntimeException {
    public StoreFailException() {
        super();
    }

    public StoreFailException(String message) {
        super(message);
    }

    public StoreFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public StoreFailException(Throwable cause) {
        super(cause);
    }
}
