package com.airbus.retex.business.exception;

public class FunctionalError extends Error {

    public FunctionalError(String message) {
        super(message);
    }

    public FunctionalError(String message, Throwable cause) {
        super(message, cause);
    }
}
