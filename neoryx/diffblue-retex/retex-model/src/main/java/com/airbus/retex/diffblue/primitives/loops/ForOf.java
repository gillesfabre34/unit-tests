package com.airbus.retex.diffblue.primitives.loops;

public class ForOf {

    public double forLetConst(double[] decimals) {
        double result = 0;
        for (double decimal: decimals) {
            result += 1;
        }
        return result;
    }
}
