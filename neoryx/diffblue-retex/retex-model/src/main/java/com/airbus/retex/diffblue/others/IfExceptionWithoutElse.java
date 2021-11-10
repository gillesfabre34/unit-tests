package com.airbus.retex.diffblue.others;

public class IfExceptionWithoutElse {

    public void ifExceptionWithoutElse(int a) throws Exception {
        if (a > 2) {
            throw new Exception("Exception");
        }
    }

}
