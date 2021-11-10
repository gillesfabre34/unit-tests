package com.airbus.retex.business.exception;

/**
 * tecnhical exception
 *
 * @author mduretti
 */
public class TechnicalError extends Error {

    /**
     *
     */
    private static final long serialVersionUID = -6072638557923522486L;

    public TechnicalError(String message) {
        super(message);
    }

    public TechnicalError(String message, Throwable cause) {
        super(message, cause);
    }
}
