package com.github.dianduiot.exception;

public class ParameterErrorException extends DoException {
    public ParameterErrorException() {
        super();
    }

    public ParameterErrorException(String message) {
        super(message);
    }
}