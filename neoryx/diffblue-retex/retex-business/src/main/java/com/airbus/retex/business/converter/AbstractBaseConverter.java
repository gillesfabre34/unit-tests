package com.airbus.retex.business.converter;

import com.airbus.retex.business.dto.CreationDto;
import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.model.basic.AbstractBaseModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @param <D> response Dto
 * @param <C> creation Dto that implements creationDto
 * @param <E> Model Entity
 */

public abstract class AbstractBaseConverter<D extends Dto, C extends CreationDto, E extends AbstractBaseModel> {

    public final ModelMapper mapperDto = new ModelMapper();

    protected final Class<D> dto;
    final Class<C> creation;
    final Class<E> entity;

    public AbstractBaseConverter(Class<D> dto, Class<C> creation, Class<E> entity) {
        this.dto = dto;
        this.creation = creation;
        this.entity = entity;
        mapperDto.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public E convert(C dto) {
        return mapperDto.map(dto, entity);
    }

    public D toDto(E entity) {
        return mapperDto.map(entity, dto);
    }

    public List<D> allToDto(List<E> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Set<E> convertAll(List<C> dtos) {
        return dtos.stream().map(this::convert).collect(Collectors.toSet());
    }
}