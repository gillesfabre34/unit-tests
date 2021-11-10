package tutorial;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IfStringNotEqualsTest {

    private IfStringNotEquals ifStringNotEqualsUnderTest;

    @BeforeEach
    void setUp() {
        ifStringNotEqualsUnderTest = new IfStringNotEquals();
    }

    @Test
    void testIfStringNotEquals() {
        // Setup

        // Run the test
        final double result = ifStringNotEqualsUnderTest.ifStringNotEquals("a");

        // Verify the results
        assertEquals(0.0, result, 0.0001);
    }
}
