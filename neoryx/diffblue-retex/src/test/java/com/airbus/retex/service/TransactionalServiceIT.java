package com.airbus.retex.service;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.helper.TransactionalService;
import com.airbus.retex.persistence.admin.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TransactionalServiceIT extends AbstractServiceIT {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionalService service;

    @Test
    public void noRollback(){

        long initialCount = userRepository.count();
        //FIXME Useless assert ? assertThat(initialCount, equalTo(8L));
        service.process();
        long count = userRepository.count();
        assertThat(count, equalTo(initialCount + 2));

    }

    @Test
    public void rollBackTransactionOnRuntimeException(){

        long initialCount = userRepository.count();
        //FIXME Useless assert ? assertThat(initialCount, equalTo(8L));
        try {
            service.processWrongId();
            Assertions.fail();
        }catch (DataIntegrityViolationException e){ // RuntimeException
            long count = userRepository.count();
            assertThat(count, equalTo(initialCount));

        }

    }

    @Test
    public void shouldRollBackTransaction_onAnyThrowable(){
        long initialCount = userRepository.count();
        //FIXME Useless assert ? assertThat(initialCount, equalTo(8L));
        Stream.of(new FunctionalException("Should rollback"), new Error("Should rollback"))
            .forEach(
                item -> {
                    try {
                        Throwable e = new Error();
                        service.processRaising(e);
                        Assertions.fail();
                    }catch (Throwable e){
                        long count = userRepository.count();
                        assertThat(count, equalTo(initialCount));
                    }
                }
            );
    }
}
