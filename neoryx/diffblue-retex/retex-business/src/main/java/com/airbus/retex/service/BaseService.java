package com.airbus.retex.service;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.model.basic.IIdentifiedModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.function.Supplier;

public abstract class BaseService<D extends Dto, E extends IIdentifiedModel> implements IService<D> {

    public BaseService(Supplier<D> dtoBuilder){
        this.dtoBuilder = dtoBuilder;
    }

    @Autowired
    private DtoConverter dtoConverter;

    @Autowired
    protected JpaRepository<E, ?> repository;

    protected Supplier<D> dtoBuilder;

    /**
     * Simple search implementation
     * Return all items from DB
     * @return
     */
    @Override
    public PageDto<D> search() {
        // default implementation : return all items from db
        Page<E> pages = repository.findAll(Pageable.unpaged());
        return new PageDto<>(dtoConverter.convert(pages.getContent(), dtoBuilder), pages.getTotalElements(), pages.getTotalPages());
    }
}
