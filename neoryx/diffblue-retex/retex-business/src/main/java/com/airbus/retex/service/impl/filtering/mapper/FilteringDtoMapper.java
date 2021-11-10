package com.airbus.retex.service.impl.filtering.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.airbus.retex.business.dto.filtering.FilteringDto;
import com.airbus.retex.model.filtering.Filtering;


@Mapper(componentModel = "spring")
public abstract class FilteringDtoMapper {

	@Mapping(source = "drt.id", target = "drtId")
            @Mapping(source = "drt.childRequest.parentRequest.origin", target = "origin")
            @Mapping(source = "physicalPart.part.partNumber", target = "partNumber")
            @Mapping(source = "physicalPart.serialNumber", target = "serialNumber")
            @Mapping(source = "physicalPart.equipmentNumber", target = "equipmentNumber")
            @Mapping(source = "physicalPart.part.partDesignation", target = "designation")
            @Mapping(source = "drt.integrationDate", target = "integrationDate")
    public abstract FilteringDto convert(Filtering filtering);

    public abstract List<FilteringDto> convert(Collection<Filtering> filterings);

}
