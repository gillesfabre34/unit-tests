package com.airbus.retex.diffblue.objects.interfaces;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.airbus.retex.diffblue.core.Cat;
import org.junit.jupiter.api.Test;

public class ParameterInterfaceTest {
    @Test
    public void testParameterInterface() {
        ParameterInterface parameterInterface = new ParameterInterface();
        assertEquals(4, parameterInterface.parameterInterface(new Cat()));
    }

    @Test
    public void testParameterInterface2() {
        Cat cat = new Cat();
        cat.setName("Name");
        assertEquals(4, (new ParameterInterface()).parameterInterface(cat));
    }
}

