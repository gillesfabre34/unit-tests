package tutorial;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IfStringEqualsTest {

    private IfStringEquals ifStringEqualsUnderTest;

    @BeforeEach
    void setUp() {
        ifStringEqualsUnderTest = new IfStringEquals();
    }

    @Test
    void testIfStringEquals() {
        // Setup

        // Run the test
        final double result = ifStringEqualsUnderTest.ifStringEquals("a");

        // Verify the results
        assertEquals(0.0, result, 0.0001);
    }
}
