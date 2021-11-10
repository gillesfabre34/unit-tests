package com.airbus.retex.diffblue.primitives.ifs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class IfIntervalTest {
    @Test
    public void testIfInterval() {
        assertEquals(1.0, (new IfInterval()).ifInterval(1.0));
        assertEquals(1.0, (new IfInterval()).ifInterval(10.0));
        assertEquals(0.0, (new IfInterval()).ifInterval(2.0));
    }
}

