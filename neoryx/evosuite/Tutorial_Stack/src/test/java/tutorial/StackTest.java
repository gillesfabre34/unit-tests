package tutorial;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StackTest {

    private Stack<T> stackUnderTest;

    @BeforeEach
    void setUp() {
        stackUnderTest = new Stack<>();
    }

    @Test
    void testPush() {
        // Setup
        final T o = null;

        // Run the test
        stackUnderTest.push(o);

        // Verify the results
    }

    @Test
    void testPop() {
        // Setup

        // Run the test
        final T result = stackUnderTest.pop();

        // Verify the results
    }

    @Test
    void testIsEmpty() {
        // Setup

        // Run the test
        final boolean result = stackUnderTest.isEmpty();

        // Verify the results
        assertTrue(result);
    }
}
