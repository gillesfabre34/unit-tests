package com.airbus.retex.business.util;

public class ConstantRegex {

    private ConstantRegex(){}

    /**
     * --------------------------------------------
     * -------------------- MAIN REGEX -------------
     * --------------------------------------------
     */
    public static final String REGEX_ALPHA_NUMBER = "^[A-Z0-9]+$";
    public static final String REGEX_NUMBER = "^[0-9]+$";

    public static final String REGEX_ALPHA_NUMBER_NULLABLE = "^[A-Z0-9]*$";
    public static final String REGEX_NUMBER_NULLABLE = "^[0-9]*$";

    /**
     * ----------------------------------------------------
     * -------------------- REGEX SPECIAL CHAR-------------
     * ----------------------------------------------------
     */
    public static final String REGEX_ALPHA_ACCENT_DASH_SPACE = "^[\\s-a-zA-ZÀ-ÖØ-öø-ÿ]+$";
}
