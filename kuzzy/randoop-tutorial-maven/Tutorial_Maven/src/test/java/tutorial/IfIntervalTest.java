package tutorial;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IfIntervalTest {

    private IfInterval ifIntervalUnderTest;

    @BeforeEach
    void setUp() {
        ifIntervalUnderTest = new IfInterval();
    }

    @Test
    void testIfInterval() {
        // Setup

        // Run the test
        final double result = ifIntervalUnderTest.ifInterval(0.0);

        // Verify the results
        assertEquals(0.0, result, 0.0001);
    }
}
