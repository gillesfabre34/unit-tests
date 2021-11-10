package com.airbus.retex.diffblue.primitives.ifs;

public class IfInterval {

    public double ifInterval(double a) {
        if (a > 1 && a < 5) {
            return 0;
        } else {
            return 1;
        }
    }
}
