package com.airbus.retex.diffblue.visibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PrivatePropertyWithAccessorsTest {
    @Test
    public void testPrivatePropertyWithAccessors() {
        assertEquals(2, (new PrivatePropertyWithAccessors()).privatePropertyWithAccessors());
    }
}

