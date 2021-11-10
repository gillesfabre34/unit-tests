package com.airbus.retex.diffblue.primitives.ifs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IfNumberInequalityTest {

    private IfNumberInequality ifNumberInequalityUnderTest;

    @BeforeEach
    void setUp() {
        ifNumberInequalityUnderTest = new IfNumberInequality();
    }

    @Test
    void testIfNumberInequality() {
        // Setup

        // Run the test
        final double result = ifNumberInequalityUnderTest.ifNumberInequality(0.0);

        // Verify the results
        assertThat(result).isEqualTo(0.0, within(0.0001));
    }
}
