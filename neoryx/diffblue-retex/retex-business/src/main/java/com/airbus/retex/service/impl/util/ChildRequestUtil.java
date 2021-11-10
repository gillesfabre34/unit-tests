package com.airbus.retex.service.impl.util;

public class ChildRequestUtil {

	/**
	 * private contructor
	 */
	private ChildRequestUtil() {

	}

    public static final String SERIAL_NUMBER_REGEX = "^[a-zA-Z0-9]+$";

    public static boolean isSerialNumberValid(final String serialNumber) {
        return serialNumber.matches(SERIAL_NUMBER_REGEX);
    }
}
