package com.airbus.retex.diffblue.primitives.ifs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.airbus.retex.diffblue.primitives.loops.ForOf;
import org.junit.jupiter.api.Test;

public class ForOfTest {
    @Test
    public void testForLetConst() {
        assertEquals(8.0, (new ForOf()).forLetConst(new double[]{10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0}));
    }
}

