package com.airbus.retex.business.mapper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.mapstruct.Mapper;
import org.mapstruct.TargetType;

import com.airbus.retex.business.dto.user.ReferenceDto;
import com.airbus.retex.model.basic.IIdentifiedModel;

@Mapper(componentModel = "spring")
public abstract class ReferenceMapper {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Can resolve an entity from its id.
     *
     * Usefull when you are mapping (manually with @Mapping) a field
     * somethingId to a field of entity Something.
     * @param primaryKey String
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T extends IIdentifiedModel<String>> T resolve(String primaryKey, @TargetType Class<T> entityClass) {
        return internalResolve(primaryKey, entityClass);
    }

    /**
     * Can resolve an entity from its id.
     *
     * Usefull when you are mapping (manually with @Mapping) a field
     * somethingId to a field of entity Something.
     * @param primaryKey Long
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T extends IIdentifiedModel<Long>> T resolve(Long primaryKey, @TargetType Class<T> entityClass) {
        return internalResolve(primaryKey, entityClass);
    }


    private <T extends IIdentifiedModel<I>, I extends Object> T internalResolve(I primaryKey, @TargetType Class<T> entityClass) {
        return primaryKey != null ? entityManager.find( entityClass, primaryKey ) : null;
    }

    /**
     * Can resolve an entity from the id embedded in reference object.
     *
     * @param reference
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T extends IIdentifiedModel<I>, I extends Object> T resolve(ReferenceDto<I> reference, @TargetType Class<T> entityClass) {
        return (reference != null && reference.getId() != null) ? entityManager.find( entityClass, reference.getId() ) : null;
    }

}