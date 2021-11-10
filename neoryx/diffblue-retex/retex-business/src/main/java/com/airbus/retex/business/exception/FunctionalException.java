package com.airbus.retex.business.exception;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class FunctionalException extends Exception {

    @Getter
	private final Map<String, Object[]> codeArgs = new HashMap<>();

    /**
     *
     */
    private static final long serialVersionUID = 8838174165991640823L;

    public FunctionalException(String code) {
        super(code);
        addMessage(code, null);
    }

    /**
     * @deprecated Use {@link #FunctionalException(String, Object...)} instead
     */
    @Deprecated(forRemoval = true)
    public FunctionalException() {
    }

    public FunctionalException(String code, Object... args) {
        super(code);
        addMessage(code, args);
    }

    public FunctionalException addMessage(String code, Object... args) {
        codeArgs.put(code, args);
        return this;
    }

}
