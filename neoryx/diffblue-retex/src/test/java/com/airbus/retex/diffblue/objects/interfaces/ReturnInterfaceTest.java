package com.airbus.retex.diffblue.objects.interfaces;

import com.airbus.retex.diffblue.core.IAnimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReturnInterfaceTest {

    private ReturnInterface returnInterfaceUnderTest;

    @BeforeEach
    void setUp() {
        returnInterfaceUnderTest = new ReturnInterface();
    }

    @Test
    void testReturnInterface() {
        // Setup

        // Run the test
        final IAnimal result = returnInterfaceUnderTest.returnInterface();

        // Verify the results
    }
}
