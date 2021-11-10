package com.airbus.retex.persistence.impl;

import com.airbus.retex.model.basic.IIdentifiedModel;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;

@Repository
public class CustomJpaRepositoryImpl<T extends IIdentifiedModel<I>, I extends Object> implements CustomJpaRepository<T> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void detach(T entity) {
        entityManager.detach(entity);
    }

    @Override
    public T refresh(T entity, String entityGraphName) {
        return refresh(entity, entityManager.getEntityGraph(entityGraphName));
    }

    @Override
    public T refresh(T entity, String[] entityGraphField) {
        EntityGraph<?> entityGraph = entityManager.createEntityGraph(getPersistentClass(entity));
        entityGraph.addAttributeNodes(entityGraphField);
        return refresh(entity, entityGraph);
    }

    @Override
    public T refresh(T entity, EntityGraph<?> entityGraph) {
        //TODO Implement with refresh method
        /*if(entityGraph == null) {
            entityManager.refresh(entity, properties(entityManager.createEntityGraph(entity.getClass())));
        } else {
            entityManager.refresh(entity, properties(entityGraph));
        }
        return entity;*/
        if(entityManager.isJoinedToTransaction()) {
            entityManager.flush();
        }
        detach(entity);
        if (entityGraph == null) {
            return (T) entityManager.find(getPersistentClass(entity), entity.getId());
        } else {
            return (T) entityManager.find(getPersistentClass(entity), entity.getId(), properties(entityGraph));
        }
    }

    @Override
    public T refresh(T entity) {
        return refresh(entity, (EntityGraph<?>)null);
    }

    private Map<String, Object> properties(EntityGraph<?> entityGraph) {
        return Map.of("javax.persistence.fetchgraph", entityGraph);
    }

    private Class<?> getPersistentClass(T entity) {
        if(entity instanceof HibernateProxy) {
            return ((HibernateProxy)entity).getHibernateLazyInitializer().getPersistentClass();
        }
        return entity.getClass();
    }
}
