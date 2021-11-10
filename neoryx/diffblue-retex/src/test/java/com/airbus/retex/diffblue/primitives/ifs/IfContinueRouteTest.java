package com.airbus.retex.diffblue.primitives.ifs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class IfContinueRouteTest {
    @Test
    public void testIfContinueRoute() {
        assertEquals(3.0, (new IfContinueRoute()).ifContinueRoute(2.0));
        assertEquals(10.0, (new IfContinueRoute()).ifContinueRoute(10.0));
    }
}

