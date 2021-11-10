package com.airbus.retex.diffblue.primitives.ifs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class IfNoSolutionTest {
    @Test
    public void testIfNoSolution() {
        assertEquals(1.0, (new IfNoSolution()).ifNoSolution(10.0));
    }
}

