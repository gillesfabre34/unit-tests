package com.airbus.retex.diffblue.primitives.ifs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class IfStringNotEqualsTest {
    @Test
    public void testIfStringNotEquals() {
        assertEquals(1.0, (new IfStringNotEquals()).ifStringNotEquals("zzz"));
        assertEquals(0.0, (new IfStringNotEquals()).ifStringNotEquals("a"));
    }
}

