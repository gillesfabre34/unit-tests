package com.airbus.retex.diffblue.visibility;

public class PrivateMethod {

    private int privateMethod(int a) {
        return 2 + a;
    }

    public int publicMethod(int a) {
        return 2 * a;
    }

}
