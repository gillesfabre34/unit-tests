package com.airbus.retex.model.admin.aircraft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class AircraftTypeTest {
    @Test
    public void testConstructor() {
        assertEquals("name", (new AircraftType("name")).getName());
    }

    @Test
    public void testAddAircraftVersion() {
        AircraftType aircraftType = new AircraftType();
        AircraftVersion aircraftVersion = new AircraftVersion();
        aircraftType.addAircraftVersion(aircraftVersion);
        assertSame(aircraftType, aircraftVersion.getAircraftType());
        assertNull(aircraftVersion.getAircraftTypeId());
    }
}

