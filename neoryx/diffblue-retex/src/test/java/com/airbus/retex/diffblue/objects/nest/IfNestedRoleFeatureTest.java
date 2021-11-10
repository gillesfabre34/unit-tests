package com.airbus.retex.diffblue.objects.nest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.model.admin.role.RoleFeature;
import org.junit.jupiter.api.Test;

public class IfNestedRoleFeatureTest {
    @Test
    public void testIfNestedRoleFeature() {
        Role role = new Role(123L);
        RoleFeature roleFeature = new RoleFeature();
        roleFeature.setRole(role);
        assertEquals(1.0, (new IfNestedRoleFeature()).ifNestedRoleFeature(roleFeature));
    }

    @Test
    public void testIfNestedRoleFeature2() {
        Role role = new Role(2L);
        RoleFeature roleFeature = new RoleFeature();
        roleFeature.setRole(role);
        assertEquals(0.0, (new IfNestedRoleFeature()).ifNestedRoleFeature(roleFeature));
    }
}

