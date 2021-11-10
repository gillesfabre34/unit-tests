package com.airbus.retex.diffblue.primitives.loops;

public class ForOfWithIf {

    public double forOfWithIf(double[] decimals) {
        double result = 0;
        for (double decimal: decimals) {
            result += decimal;
            if (result > 3) {
                return 1;
            }
        }
        return result;
    }
}
