package com.github.dianduiot.exception;

public class DoException extends Exception {
    public DoException() {
        super();
    }

    public DoException(String message) {
        super(message);
    }

    public DoException(Throwable cause) {
        super(cause);
    }
}