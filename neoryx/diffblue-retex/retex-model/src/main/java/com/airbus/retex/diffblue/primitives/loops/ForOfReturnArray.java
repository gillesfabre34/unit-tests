package com.airbus.retex.diffblue.primitives.loops;

import java.util.ArrayList;

public class ForOfReturnArray {

    public ArrayList<Double> forOfReturnArray(double[] decimals) {
        ArrayList<Double> result = new ArrayList<>();
        for (double decimal: decimals) {
            result.add(decimal);
        }
        return result;
    }
}
