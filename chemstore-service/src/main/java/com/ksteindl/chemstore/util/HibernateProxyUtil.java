package com.ksteindl.chemstore.util;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

public class HibernateProxyUtil {

    public static <T> T unproxy(T entity) {
        if (entity == null) {
            throw new
                    NullPointerException("Entity passed for initialization is null");
        }
        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer()
                    .getImplementation();
        }
        return entity;
    }
}
