package com.airbus.retex.model.admin.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.airbus.AirbusEntity;
import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.user.User;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

public class RoleTest {
    @Test
    public void testConstructor() {
        assertEquals(EnumActiveState.ACTIVE, (new Role()).getState());
    }

    @Test
    public void testConstructor2() {
        Role actualRole = new Role(123L);
        assertEquals(123L, actualRole.getId().longValue());
        assertEquals(EnumActiveState.ACTIVE, actualRole.getState());
    }

    @Test
    public void testAddRoleFeature() {
        Role role = new Role();
        RoleFeature roleFeature = new RoleFeature();
        role.addRoleFeature(roleFeature);
        assertSame(role, roleFeature.getRole());
    }

    @Test
    public void testRemoveRoleFeature() {
        Role role = new Role();
        RoleFeature roleFeature = new RoleFeature();
        role.removeRoleFeature(roleFeature);
        assertNull(roleFeature.getRole());
    }

    @Test
    public void testSetFeatureCode() {
        RoleFeature roleFeature = new RoleFeature(123L, FeatureCode.PART, EnumRightLevel.NONE);
        assertEquals(1.0, (new Role()).setFeatureCode(roleFeature));
    }

    @Test
    public void testSetFeatureCode2() {
        RoleFeature roleFeature = new RoleFeature(123L, FeatureCode.ADMIN, EnumRightLevel.NONE);
        assertEquals(0.0, (new Role()).setFeatureCode(roleFeature));
    }

    @Test
    public void testSetFeatureCode3() {
        Role role = new Role(123L);
        assertEquals(0.0, role.setFeatureCode(new RoleFeature(123L, FeatureCode.ADMIN, EnumRightLevel.NONE)));
    }

    @Test
    public void testSetAirbusEntity() {
        Role role = new Role();
        AirbusEntity airbusEntity = new AirbusEntity();
        role.setAirbusEntity(airbusEntity);
        assertSame(airbusEntity, role.getAirbusEntity());
    }

    @Test
    public void testSetFeatures() {
        Role role = new Role();
        HashSet<RoleFeature> roleFeatureSet = new HashSet<RoleFeature>();
        role.setFeatures(roleFeatureSet);
        assertSame(roleFeatureSet, role.getFeatures());
    }

    @Test
    public void testSetRoleCode() {
        Role role = new Role();
        role.setRoleCode(RoleCode.TECHNICAL_RESPONSIBLE);
        assertEquals(RoleCode.TECHNICAL_RESPONSIBLE, role.getRoleCode());
    }

    @Test
    public void testSetUsers() {
        Role role = new Role();
        HashSet<User> userSet = new HashSet<User>();
        role.setUsers(userSet);
        assertSame(userSet, role.getUsers());
    }
}

