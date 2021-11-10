package com.airbus.retex.diffblue.visibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PrivatePropertyWithoutAccessorsWithAccessorsTest {
    @Test
    public void testPrivatePropertyWithAccessors() {
        assertEquals(2, (new PrivatePropertyWithAccessors()).privatePropertyWithAccessors());
    }
}

