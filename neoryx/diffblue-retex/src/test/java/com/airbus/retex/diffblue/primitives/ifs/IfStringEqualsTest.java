package com.airbus.retex.diffblue.primitives.ifs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class IfStringEqualsTest {
    @Test
    public void testIfStringEquals() {
        assertEquals(0.0, (new IfStringEquals()).ifStringEquals("zzz"));
        assertEquals(1.0, (new IfStringEquals()).ifStringEquals("a"));
    }
}

