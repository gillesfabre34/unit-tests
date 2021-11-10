package com.airbus.retex.diffblue.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CatTest {
    @Test
    public void testNumberOfLegs() {
        assertEquals(4, (new Cat()).numberOfLegs());
    }
}

