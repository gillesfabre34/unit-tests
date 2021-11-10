package com.airbus.retex.business.exception;

/**
 * Exception for validation
 *
 * @author mduretti
 */
public class ValidationException extends FunctionalException {

    /**
     *
     */
    private static final long serialVersionUID = 395176682248430576L;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
