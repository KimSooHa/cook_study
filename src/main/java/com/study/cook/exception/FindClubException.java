package com.study.cook.exception;

import com.study.cook.enums.ParticipateFailReason;

import static com.study.cook.enums.ParticipateFailReason.UNKNOWN;

public class FindClubException extends IllegalArgumentException {

    private final ParticipateFailReason reason;

    public FindClubException(String message) {
        super(message);
        this.reason = UNKNOWN;
    }

    public FindClubException(String message, Throwable cause) {
        super(message, cause);
        this.reason = UNKNOWN;
    }
    public FindClubException(String message, ParticipateFailReason reason) {
        super(message);
        this.reason = reason;
    }

    public FindClubException(Throwable cause) {
        super(cause);
        this.reason = UNKNOWN;
    }
}
