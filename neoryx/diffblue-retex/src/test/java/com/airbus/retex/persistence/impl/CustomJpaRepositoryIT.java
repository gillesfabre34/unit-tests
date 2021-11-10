package com.airbus.retex.persistence.impl;

import com.airbus.retex.BaseRepositoryTest;
import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.damage.Damage;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.admin.UserRepository;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TestTransaction;

import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
public class CustomJpaRepositoryIT extends BaseRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public static void beforeAll() throws NoSuchFieldException {
        Damage.class.getDeclaredField("classification").setAccessible(true);
    }

    @Test
    public void refresh_lazyFieldNotInitialized() throws Exception {
        User user = userRepository.refresh(userRepository.getById(dataset.user_simpleUser.getId()));

        TestTransaction.end();

        assertThrows(LazyInitializationException.class, () -> {
            user.getRoles().size();
        });
    }

    @Test
    public void refresh_withEntityGraph_lazyFieldInitialized() throws Exception {
        User user = userRepository.refresh(
                userRepository.getById(dataset.user_simpleUser.getId())
                , new String[]{"roles"});

        TestTransaction.end();

        User finalUser = user;
        assertDoesNotThrow(() -> {
            finalUser.getRoles().size();
        });
    }

    @Test
    public void save_lazyFieldInitialized () {
        User user = new User();
        user.setEmail("test@airbus.com");
        user.setFirstName("Firstname");
        user.setLastName("Lastname ");
        user.setLanguage(Language.FR);
        user.setState(EnumActiveState.ACTIVE);
        user.setStaffNumber("A9999999");
        user.setAirbusEntityId(dataset.airbusEntity_france.getId());


        User savedUser = userRepository.save(user);

        assertThat(Hibernate.isInitialized(savedUser.getUserFeatures()), equalTo(true));
    }

}
