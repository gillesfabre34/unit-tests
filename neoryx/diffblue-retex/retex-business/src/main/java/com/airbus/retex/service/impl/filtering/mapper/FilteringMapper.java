package com.airbus.retex.service.impl.filtering.mapper;

import com.airbus.retex.business.dto.filtering.FilteringUpdateDto;
import com.airbus.retex.business.mapper.AbstractMapper;
import com.airbus.retex.business.mapper.ReferenceMapper;
import com.airbus.retex.model.filtering.Filtering;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring", uses = {ReferenceMapper.class})
public abstract class FilteringMapper extends AbstractMapper {

    public abstract void update(FilteringUpdateDto source, @MappingTarget Filtering target);
}
