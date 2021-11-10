package com.airbus.retex.repository;

import com.airbus.retex.BaseRepositoryTest;
import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.persistence.admin.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RoleRepositoryUT extends BaseRepositoryTest {

    @Autowired
    private RoleRepository roleDao;

    @Test
    public void revokeRole_shouldPass() {
        List<Role> list = roleDao.findAll();
        int updated = roleDao.revokeRole(list.get(0).getId());
        assertThat(updated, equalTo(1));
    }

    @Test
    public void revokeRoleWithTheGoodEntity_thenRevokeDoesNotWork() {
        List<Role> list = roleDao.findAll();
        int updated = roleDao.revokeRole(list.get(0).getId(), list.get(0).getAirbusEntity().getId());
        assertThat(updated, equalTo(1));
    }

    @Test
    public void revokeRoleOfOtherEntity_thenRevokeDoesNotWork() {
        List<Role> list = roleDao.findAll();
        int updated = roleDao.revokeRole(list.get(list.size()-1).getId(), list.get(1).getAirbusEntity().getId());
        assertThat(updated, equalTo(0));
    }
}
