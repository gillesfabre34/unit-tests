package com.airbus.retex.diffblue.primitives.ifs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class IfAndDependentNestedInclusionTest {
    @Test
    public void testIfAndDependentNestedInclusion() {
        assertEquals(2.0, (new IfAndDependentNestedInclusion()).ifAndDependentNestedInclusion(2.0));
        assertEquals(0.0, (new IfAndDependentNestedInclusion()).ifAndDependentNestedInclusion(1.0));
        assertEquals(1.0, (new IfAndDependentNestedInclusion()).ifAndDependentNestedInclusion(10.0));
    }
}

