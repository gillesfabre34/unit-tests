package com.airbus.retex.diffblue.others;

public class IfException {

    public int ifException(int a) throws Exception {
        if (a > 2) {
            throw new Exception("Exception");
        } else {
            return 2;
        }
    }

}
