package com.airbus.retex.service.impl.util;

import java.lang.reflect.InvocationTargetException;

public class ClassUtil {
	/**
	 * private contructor
	 */
	private ClassUtil() {

	}

    public static <T extends Object> T instanciate(Class<T> clazz) {
        T instance = null;

        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
        return instance;
    }
}
