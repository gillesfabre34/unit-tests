package com.airbus.retex.diffblue.primitives.ifs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class IfStringEqualityTest {
    @Test
    public void testIfStringEquality() {
        assertEquals(1.0, (new IfStringEquality()).ifStringEquality("a"));
    }
}

