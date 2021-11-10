package com.airbus.retex.admin;

import com.airbus.retex.model.common.EnumRightLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RightLevelUT {
    @Test
    public void asserts() {
        assertTrue(EnumRightLevel.values().length == 3);
        assertTrue(EnumRightLevel.NONE.ordinal() < EnumRightLevel.READ.ordinal());
        assertTrue(EnumRightLevel.READ.ordinal() < EnumRightLevel.WRITE.ordinal());
    }
}
