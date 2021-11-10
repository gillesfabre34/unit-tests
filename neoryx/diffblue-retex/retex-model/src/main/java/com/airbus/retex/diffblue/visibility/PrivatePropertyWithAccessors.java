package com.airbus.retex.diffblue.visibility;

public class PrivatePropertyWithAccessors {

    private int a;

    public int getA() {
        return a;
    }

    public int privatePropertyWithAccessors() {
        return 2 + a;
    }

}
