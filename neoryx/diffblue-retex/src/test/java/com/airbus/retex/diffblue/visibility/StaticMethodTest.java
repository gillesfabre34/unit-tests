package com.airbus.retex.diffblue.visibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StaticMethodTest {
    @Test
    public void testStaticMethod() {
        assertEquals(3, StaticMethod.staticMethod(1));
    }
}

