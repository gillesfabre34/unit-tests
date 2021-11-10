package com.airbus.retex.business.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.airbus.retex.business.dto.CreationDto;
import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.business.dto.user.IIdentifiableDto;
import com.airbus.retex.business.dto.user.UserFullDto;
import com.airbus.retex.model.basic.IIdentifiedModel;
import com.airbus.retex.model.basic.IModel;
import com.airbus.retex.model.user.User;

@Component
public class DtoConverter {

    private static final Class[] DTO_SUPPORTED_TYPES = new Class[]{
            String.class, Integer.class, Long.class, Float.class, Double.class,
            LocalDateTime.class, LocalDate.class, Enum.class
    };

    @Autowired
    private EntityManager entityManager;

    private final ModelMapper normalMapper = new ModelMapper();

    private final ModelMapper primitiveOnlyMapper = new ModelMapper();

    private ThreadLocal<Long> revisionThreadLocal = new ThreadLocal<>();

    @PostConstruct
    public void postContruct() {
        primitiveOnlyMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        primitiveOnlyMapper.getConfiguration().setPropertyCondition(new Condition() {
            @Override
            public boolean applies(MappingContext context) {
                Class<?> destClazz = context.getDestinationType();
                for (var clazz : DTO_SUPPORTED_TYPES) {
                    if (clazz.isAssignableFrom(destClazz)) {
                        return true;
                    }
                }
                return false;
            }
        });

        normalMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        // -------------------------------------------------------------------
        // ----------------------------- USER --------------------------------
        // -------------------------------------------------------------------
		normalMapper.createTypeMap(User.class, UserFullDto.class)
				.addMappings(mapping -> mapping.map(User::getUserFeatures, UserFullDto::setFeatures));
    }


    /*
     * Deprecated methods
     */

    /**
     * @deprecated
     */
    @Deprecated(forRemoval = true)
    public <D extends Dto, E extends IIdentifiedModel> List<E> toEntities(Collection<D> dtos, Supplier<E> entityBuilder) {
        return dtos.stream().map(dto -> toEntity(dto, entityBuilder)).collect(Collectors.toList());
    }

    /**
     * @deprecated Use {@link #createEntity(CreationDto, Class)} or {@link #updateEntity(Dto, IIdentifiedModel)} instead
     */
    @Deprecated(forRemoval = true)
    public <D extends Dto, E extends IIdentifiedModel> E toEntity(D dto, Supplier<E> entityBuilder) {
        E entity = entityBuilder.get();
        normalMapper.map(dto, entity);
        return entity;
    }

    /**
     * @deprecated Use {@link #toDtos(Collection, Class)} instead
     */
    @Deprecated(forRemoval = true)
    public <D extends Dto> List<D> convert(Collection<? extends IIdentifiedModel> entities, Supplier<D> dtoBuilder) {
        return convert(entities, dtoBuilder, null);
    }

    /**
     * @deprecated Use {@link #toDtos(Collection, Class, Long)} instead
     */
    @Deprecated(forRemoval = true)
    public <E extends IIdentifiedModel, D extends Dto> List<D> convert(Collection<E> entities, Supplier<D> dtoBuilder, Long revision) {
        return entities.stream().map(entity -> convert(entity, dtoBuilder, revision)).collect(Collectors.toList());
    }

    /**
     * @deprecated Use {@link #toDto(IModel, Class)} instead
     */
    @Deprecated(forRemoval = true)
    public <D extends Dto> D convert(IIdentifiedModel entity, Supplier<D> dtoBuilder) {
        return convert(entity, dtoBuilder, null);
    }

    /**
     * @deprecated Use {@link #toDto(IModel, Class, Long)} instead
     */
    @Deprecated(forRemoval = true)
    public <D> D convert(IIdentifiedModel entity, Supplier<D> dtoBuilder, Long revision) {
        D dto = dtoBuilder.get();
        revisionThreadLocal.set(revision);
        normalMapper.map(entity, dto);
        revisionThreadLocal.set(null);
        return dto;
    }

    /*
     * New methods
     */
    public <D extends Dto> List<D> toDtos(Collection<? extends IModel> entities, Class<D> dtoClazz) {
        return toDtos(entities, dtoClazz, null);
    }

    public <D extends Dto> List<D> toDtos(Collection<? extends IModel> entities, Class<D> dtoClazz, Long revision) {
        return entities.stream().map(dto -> toDto(dto, dtoClazz, revision)).collect(Collectors.toList());
    }

    public <D extends Dto> D toDto(IModel entity, Class<D> dtoClazz) {
        if(entity == null) {
            return  null;
        } else {
            return toDto(entity, dtoClazz, null);
        }
    }

    public <D extends Dto> D toDto(IModel entity, Class<D> dtoClazz, Long revision) {
        TranslateDto.setCurrentRevision(revision);
        D result = normalMapper.map(entity, dtoClazz);
        TranslateDto.setCurrentRevision(null);
        return result;
    }

    /**
     * Create a new entity of type E based on a Dto. All fields (basic or not) and all collections are mapped.
     */
    public <D extends CreationDto, E extends IModel> E createEntity(D dto, Class<? extends E> clazz) {
        return normalMapper.map(dto, clazz);
    }

    /**
     * Update an existing entity of type E based on a Dto. Only basic fields are mapped, see {@link #DTO_SUPPORTED_TYPES}.
     * <p>
     * To update correctly relationship, you'll have to use the correct methods among:
     * - {@link #mapIdsAdd(Collection, Collection, Class)}
     * - {@link #mapUpdate(Object, Consumer, Class)}
     * - {@link #mapIdsAddRemove(Collection, Collection, Class)}
     * - {@link #mapEntitiesAddUpdate(Collection, Collection, Class)}
     * - {@link #mapEntitiesAddUpdateRemove(Collection, Collection, Class)}
     */
    public <D extends Dto, I, E extends IIdentifiedModel<I>> void updateEntity(D dto, E entity) {
        primitiveOnlyMapper.map(dto, entity);
    }

    /**
     * Update a *ToOne relatioship.
     * <p>
     * The target field is assign using the setter and by retrieving the Hibernate's entity reference
     */
    public <I, E extends IIdentifiedModel<I>> void mapUpdate(I id, Consumer<E> targetSetter, Class<? extends E> clazz) {
        targetSetter.accept(entityManager.getReference(clazz, id));
    }

    /**
     * Update a *ToMany relationship.
     * <p>
     * Using the list of ids, entities are added to the given target list by retrieving the Hibernate's entity reference.
     */
    public <I, E extends IIdentifiedModel<I>> void mapIdsAdd(Collection<I> sourceIdList, Collection<E> targetList, Class<? extends E> clazz) {
        for (var id : sourceIdList) {
            targetList.add(entityManager.getReference(clazz, id));
        }
    }

    /**
     * Call {@link #mapIdsAdd(Collection, Collection, Class)} and {@link #mapIdsAddRemove(Collection, Collection, Class)}.
     */
    public <I, E extends IIdentifiedModel<I>> void mapIdsAddRemove(Collection<I> sourceIdList, Collection<E> targetList, Class<? extends E> clazz) {
        mapIdsRemove(sourceIdList, targetList);

        mapIdsAdd(sourceIdList, targetList, clazz);
    }

    /**
     * Remove from the target list entities for which the id is not in the source id list.
     */
    public <I, E extends IIdentifiedModel<I>> void mapIdsRemove(Collection<I> sourceIdList, Collection<E> targetList) {
        mapIdsRemove(sourceIdList, targetList, false);
    }

    public <I, E extends IIdentifiedModel<I>> void mapIdsRemove(Collection<I> sourceIdList, Collection<E> targetList, boolean targetOneToMany) {
        Iterator<E> it = targetList.iterator();
        while (it.hasNext()) {
            E entity = it.next();
            if (!sourceIdList.contains(entity.getId())) {
                it.remove();
                if (targetOneToMany) {
                    entityManager.remove(entity);
                }
            }
        }
    }

    /**
     * Update a *ToMany relationship and all entities of the relationship.
     * <p>
     * Using the list of dtos, entities are added and updated to the given target list by retrieving the Hibernate's entity reference for existing entities.
     */
    public <D extends IIdentifiableDto<I>, I, E extends IIdentifiedModel<I>> void mapEntitiesAddUpdate(Collection<D> sourceList, Collection<E> targetList, Class<? extends E> clazz) {
        for (var source : sourceList) {
            E target;
            if (source.getId() == null) {
                target = primitiveOnlyMapper.map(source, clazz);
            } else {
                target = entityManager.getReference(clazz, source.getId());
                primitiveOnlyMapper.map(source, target);
                entityManager.merge(target);
            }
            targetList.add(target);
        }
    }

    /**
     * Update a *ToMany relationship and all entities of the relationship.
     * <p>
     * Do the same as {@link #mapEntitiesAddUpdate(Collection, Collection, Class)} and also remove entities that are not in the dtos list.
     */
    public <D extends IIdentifiableDto<I>, I, E extends IIdentifiedModel<I>> void mapEntitiesAddUpdateRemove(Collection<D> sourceList, Collection<E> targetList, Class<? extends E> clazz) {
        List<I> sourceIdList = sourceList.stream().map(IIdentifiableDto::getId).collect(Collectors.toList());

        mapIdsRemove(sourceIdList, targetList);

        mapEntitiesAddUpdate(sourceList, targetList, clazz);
    }
}
