package com.airbus.retex.service.impl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RetexStreamUtilTU {

    private Set<Long> testSet;

    @BeforeEach
    public void init() {
        testSet = Set.of(1L,2L,4L);
    }

    @Test
    public void shouldReturnEmpty(){
        assertThat(testSet.stream().filter(aLong -> aLong.equals(3L)).collect(RetexStreamUtil.findOneOrEmpty()), equalTo(Optional.empty()));
    }

    @Test
    public void shouldReturnOneOptional(){
        assertThat(testSet.stream().filter(aLong -> aLong.equals(1L)).collect(RetexStreamUtil.findOneOrEmpty()), equalTo(Optional.of(1L)));
    }

    @Test
    public void shouldThrowIllegalStateException(){
        assertThrows(IllegalStateException.class, () -> testSet.stream().filter(aLong -> aLong > 1L).collect(RetexStreamUtil.findOneOrEmpty()));
    }

}
