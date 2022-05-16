package com.alwa.spread.exception;

public class SpreadException extends RuntimeException {

    public SpreadException(String message) {
        super(message);
    }

    public SpreadException(String message, Throwable e) {
        super(message, e);
    }

}
