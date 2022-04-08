package com.alwa.spread;

public class SpreaderException extends RuntimeException {

    public SpreaderException(String message) {
        super(message);
    }

    public SpreaderException(String message, Throwable e) {
        super(message, e);
    }

}
