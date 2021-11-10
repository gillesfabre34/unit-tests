package com.airbus.retex.diffblue.objects.instance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.airbus.retex.diffblue.core.Cat;
import com.airbus.retex.diffblue.objects.interfaces.ReturnInterface;
import org.junit.jupiter.api.Test;

public class ReturnInterfaceTest {
    @Test
    public void testReturnInterface() {
        assertEquals("Biela", ((Cat) (new ReturnInterface()).returnInterface()).getName());
    }
}

