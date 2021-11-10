package com.airbus.retex.business.util;

import com.airbus.retex.business.exception.FunctionalException;

public interface ThrowingConsumer<T, E extends FunctionalException> {

    void accept(T t) throws E;
}
