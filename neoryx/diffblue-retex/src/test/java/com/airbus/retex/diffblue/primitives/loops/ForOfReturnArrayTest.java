package com.airbus.retex.diffblue.primitives.loops;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class ForOfReturnArrayTest {
    @Test
    public void testForOfReturnArray() {
    }

    @Test
    public void testForOfReturnArray2() {
        ArrayList<Double> actualForOfReturnArrayResult = (new ForOfReturnArray())
                .forOfReturnArray(new double[]{10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0});
        assertEquals(8, actualForOfReturnArrayResult.size());
    }
}

