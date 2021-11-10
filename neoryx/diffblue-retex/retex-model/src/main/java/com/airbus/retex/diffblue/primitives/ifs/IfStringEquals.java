package com.airbus.retex.diffblue.primitives.ifs;

public class IfStringEquals {

    public double ifStringEquals(String a) {
        if (a.equals("zzz")) {
            return 0;
        } else {
            return 1;
        }
    }
}
