package com.study.cook.exception;

import com.study.cook.enums.ParticipateFailReason;

import static com.study.cook.enums.ParticipateFailReason.*;

public class ParticipateFailException extends RuntimeException {

    private final ParticipateFailReason reason;

    public ParticipateFailException(String message) {
        super(message);
        this.reason = UNKNOWN;
    }

    public ParticipateFailException(String message, Throwable cause) {
        super(message, cause);
        this.reason = UNKNOWN;
    }

    public ParticipateFailException(Throwable cause) {
        super(cause);
        this.reason = UNKNOWN;
    }

    public ParticipateFailException(String message, ParticipateFailReason reason) {
        super(message);
        this.reason = reason;
    }

    public ParticipateFailReason getReason() {
        return reason;
    }
}
