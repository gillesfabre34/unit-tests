package com.airbus.retex.diffblue.primitives.ifs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IfNumberEqualityTest {

    private IfNumberEquality ifNumberEqualityUnderTest;

    @BeforeEach
    void setUp() {
        ifNumberEqualityUnderTest = new IfNumberEquality();
    }

    @Test
    void testIfNumberEquality() {
        // Setup

        // Run the test
        final double result = ifNumberEqualityUnderTest.ifNumberEquality(0.0);

        // Verify the results
        assertThat(result).isEqualTo(0.0, within(0.0001));
    }
}
