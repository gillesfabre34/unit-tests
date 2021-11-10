package com.airbus.retex.persistence;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

import javax.persistence.EntityManager;

import org.hibernate.EmptyInterceptor;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;

import com.airbus.retex.model.basic.AbstractVersionableChildModel;

public class EntityInterceptor extends EmptyInterceptor {

    @Autowired
    private EntityManager em;

    @Override
    public boolean onSave(
            Object entity,
            Serializable id,
            Object[] state,
            String[] propertyNames,
            Type[] types
    ) {
        if (AbstractVersionableChildModel.class.isAssignableFrom(entity.getClass())) {
            Long naturalId = this.getNextNaturalId();
            int key = Arrays.asList(propertyNames).lastIndexOf("naturalId");

            AbstractVersionableChildModel versionableEntity = (AbstractVersionableChildModel) entity;

            if (-1 != key && null == versionableEntity.getNaturalId()) {
                state[key] = naturalId;
                versionableEntity.setNaturalId(naturalId);
                return true;
            }
        }

        return false;
    }


    @Override
    public void preFlush(Iterator entities) {
        entities.forEachRemaining(entity -> {});
    }

    /**
     * @return
     */
    private Long getNextNaturalId() {
        Session session = em.unwrap(Session.class);
        FlushMode originalFlushMode = session.getHibernateFlushMode();
        session.setHibernateFlushMode(FlushMode.MANUAL); //TODO find something better

        Object nextId = em
                .createNativeQuery("SELECT NEXT VALUE FOR global_functional_id_seq")
                .getSingleResult();

        session.setHibernateFlushMode(originalFlushMode);

        return Long.valueOf(nextId.toString());
    }

}
