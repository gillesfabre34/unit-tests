package com.airbus.retex.controller;

import static org.junit.jupiter.api.Assertions.assertNull;

import com.airbus.retex.service.IService;
import org.junit.jupiter.api.Test;

public class BaseControllerDiffBlueTest {
    @Test
    public void testSearchSupplier() {
        // TODO: This test is incomplete.
        //   Reason: Unable to find any meaningful assertion.
        //   Please add getters to the class under test that return fields written
        //   by the method under test.

        (new BaseControllerDiffBlue<IService>()).searchSupplier();
    }

    @Test
    public void testSearchSupplier2() {
        assertNull((new BaseControllerDiffBlue<IService>()).searchSupplier());
    }
}

