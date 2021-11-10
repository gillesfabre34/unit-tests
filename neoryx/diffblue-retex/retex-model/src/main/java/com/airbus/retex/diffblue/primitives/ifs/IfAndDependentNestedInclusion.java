package com.airbus.retex.diffblue.primitives.ifs;

public class IfAndDependentNestedInclusion {

    public double ifAndDependentNestedInclusion(double a) {
        if (a < 5) {
            if (a < 2) {
                return 0;
            } else {
                return 2;
            }
        } else {
            return 1;
        }
    }
}
