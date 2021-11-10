package com.airbus.retex.diffblue.objects.nest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.role.RoleFeature;
import com.airbus.retex.model.common.EnumRightLevel;
import org.junit.jupiter.api.Test;

public class IfNestedEnumTest {
    @Test
    public void testIfNestedEnum() {
        IfNestedEnum ifNestedEnum = new IfNestedEnum(123L);
        assertEquals(0.0, ifNestedEnum.ifNestedEnum(new RoleFeature(123L, FeatureCode.ADMIN, EnumRightLevel.NONE)));
    }

    @Test
    public void testIfNestedEnum2() {
        RoleFeature roleFeature = new RoleFeature(123L, FeatureCode.ADMIN, EnumRightLevel.NONE);
        assertEquals(0.0, (new IfNestedEnum()).ifNestedEnum(roleFeature));
    }

    @Test
    public void testIfNestedEnum3() {
        RoleFeature roleFeature = new RoleFeature(123L, FeatureCode.PART, EnumRightLevel.NONE);
        assertEquals(1.0, (new IfNestedEnum()).ifNestedEnum(roleFeature));
    }
}

