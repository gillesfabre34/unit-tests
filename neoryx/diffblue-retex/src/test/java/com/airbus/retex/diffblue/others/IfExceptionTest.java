package com.airbus.retex.diffblue.others;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class IfExceptionTest {
    @Test
    public void testIfException() throws Exception {
        assertEquals(2, (new IfException()).ifException(1));
    }
}

