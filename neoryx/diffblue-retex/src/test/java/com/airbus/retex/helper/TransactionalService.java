package com.airbus.retex.helper;

import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.admin.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class TransactionalService {
    @Autowired
    private UserRepository userRepository;

    /**
     * Successful
     */
    public void process(){
        createUser(10L);
        createUser(11L);
    }

    /**
     * DB constraint exception
     */
    public void processWrongId(){
        createUser(10L);
        createUser(10L);
    }

    /**
     * Method used to alter DB
     * @param id
     */
    private void createUser(Long id) {
        User user = new User();
        user.setEmail("user" + id + "@airbus.com");
        user.setFirstName("Firstname ");
        user.setLastName("Lastname ");
        user.setLanguage(Language.FR);
        user.setState(EnumActiveState.ACTIVE);
        user.setStaffNumber("A00000");
        user.setAirbusEntityId(1L);
        userRepository.save(user);
    }

    /**
     * Raise according to the given Throwable
     */
    public void processRaising(Throwable e) throws Throwable {
        process();
        throw e;
    }
}
