package com.airbus.retex.model.admin.aircraft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class AircraftFamilyTest {
    @Test
    public void testConstructor() {
        assertEquals("name", (new AircraftFamily("name")).getName());
    }

    @Test
    public void testAddAircraftType() {
        AircraftFamily aircraftFamily = new AircraftFamily();
        AircraftType aircraftType = new AircraftType();
        aircraftFamily.addAircraftType(aircraftType);
        assertNull(aircraftType.getAircraftFamilyId());
        assertSame(aircraftFamily, aircraftType.getAircraftFamily());
    }
}

