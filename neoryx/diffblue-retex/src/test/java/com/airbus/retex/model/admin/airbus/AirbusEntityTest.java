package com.airbus.retex.model.admin.airbus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class AirbusEntityTest {
    @Test
    public void testSetCode() {
        AirbusEntity airbusEntity = new AirbusEntity();
        airbusEntity.setCode("code");
        assertEquals("code", airbusEntity.getCode());
    }

    @Test
    public void testSetCountryName() {
        AirbusEntity airbusEntity = new AirbusEntity();
        airbusEntity.setCountryName("countryName");
        assertEquals("countryName", airbusEntity.getCountryName());
    }
}

