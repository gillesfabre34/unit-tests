package com.airbus.retex.service.impl.util;

import org.hibernate.proxy.HibernateProxy;

public class HibernateUtil {

	/**
	 * private contructor
	 */
	private HibernateUtil() {

	}

    public static <C> Class<C> getPersistentClass(C entity) {
        if(entity instanceof HibernateProxy) {
            return ((HibernateProxy)entity).getHibernateLazyInitializer().getPersistentClass();
        }
        return (Class<C>) entity.getClass();
    }

}
