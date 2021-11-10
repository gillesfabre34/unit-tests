package com.airbus.retex.service.impl.request.mapper;

import com.airbus.retex.business.dto.request.RequestFullDto;
import com.airbus.retex.business.mapper.AbstractMapper;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.service.impl.request.RequestServiceImpl;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class RequestFullDtoMapper extends AbstractMapper {

    @Autowired
    RequestServiceImpl requestService;


	@Mapping(source = "origin.id", target = "originId")
	@Mapping(source = "ata.code", target = "ataCode")
	@Mapping(source = "originUrl", target = "originURL")
	@Mapping(source = "version", target = "versionNumber")
	@Mapping(source = "originMedias", target = "originMedias")
	@Mapping(source = "specMedias", target = "specMedias")
    public abstract RequestFullDto convert(Request request);

    @AfterMapping
    void updateIsDeletable(final Request request, @MappingTarget final RequestFullDto dto) {
        dto.setIsDeletable(requestService.checkIsDeletable(request).isEmpty());
    }
}
