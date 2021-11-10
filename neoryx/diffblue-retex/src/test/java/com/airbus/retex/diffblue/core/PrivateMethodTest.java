package com.airbus.retex.diffblue.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.airbus.retex.diffblue.visibility.PrivateMethod;
import org.junit.jupiter.api.Test;

public class PrivateMethodTest {
    @Test
    public void testMultiply() {
        assertEquals(2, (new PrivateMethod()).publicMethod(1));
    }
}

