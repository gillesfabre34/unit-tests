package com.airbus.retex.diffblue.visibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PrivatePropertyWithoutAccessorsTest {

    @Test
    public void testPrivatePropertyWithoutAccessors() {
        assertEquals(2, (new PrivatePropertyWithoutAccessors()).privatePropertyWithoutAccessors());
    }
}

