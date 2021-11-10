package com.airbus.retex.business.exception;

public class NotFoundException extends FunctionalException {

    public NotFoundException(String code, Object... args) {
        super(code, args);
    }
}
