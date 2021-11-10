package com.airbus.retex.diffblue.primitives.loops;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ForOfWithIfTest {
    @Test
    public void testForOfWithIf() {
        assertEquals(1.0, (new ForOfWithIf()).forOfWithIf(new double[]{10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0}));
        assertEquals(Double.NaN,
                (new ForOfWithIf()).forOfWithIf(new double[]{Double.NaN, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0}));
        assertEquals(1.0, (new ForOfWithIf()).forOfWithIf(new double[]{0.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0}));
    }
}

