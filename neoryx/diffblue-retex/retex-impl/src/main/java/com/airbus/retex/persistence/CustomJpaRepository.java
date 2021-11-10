package com.airbus.retex.persistence;

import javax.persistence.EntityGraph;

public interface CustomJpaRepository<T> {

    void detach(T entity);

    T refresh(T entity, String entityGraphName);

    T refresh(T entity, String[] entityGraphField);

    T refresh(T entity, EntityGraph<?> entityGraph);

    T refresh(T entity);
}
