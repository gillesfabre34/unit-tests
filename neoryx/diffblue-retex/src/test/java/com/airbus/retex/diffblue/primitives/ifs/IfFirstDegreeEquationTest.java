package com.airbus.retex.diffblue.primitives.ifs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class IfFirstDegreeEquationTest {
    @Test
    public void testIfFirstDegreeEquation() {
        assertEquals(1.0, (new IfFirstDegreeEquation()).ifFirstDegreeEquation(10.0));
    }
}

