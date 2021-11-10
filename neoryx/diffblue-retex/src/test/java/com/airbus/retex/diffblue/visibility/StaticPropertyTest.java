package com.airbus.retex.diffblue.visibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StaticPropertyTest {
    @Test
    public void testStaticProperty() {
        assertEquals(2, (new StaticProperty()).staticProperty());
    }
}

