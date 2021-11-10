package tutorial;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(0.0, result, 0.0001);
    }
}
