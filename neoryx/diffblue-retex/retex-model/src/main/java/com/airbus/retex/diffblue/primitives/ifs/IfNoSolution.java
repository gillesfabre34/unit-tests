package com.airbus.retex.diffblue.primitives.ifs;

public class IfNoSolution {

    public double ifNoSolution(double a) {
        if (a * a < 0) {
            return 0;
        } else {
            return 1;
        }
    }
}
