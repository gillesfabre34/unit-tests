package com.airbus.retex.service.impl.request.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.airbus.retex.business.dto.request.RequestDto;
import com.airbus.retex.business.dto.user.UserLightDto;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.user.User;
import com.airbus.retex.service.impl.request.RequestServiceImpl;

@Mapper(componentModel = "spring")
public abstract class RequestDtoMapper {

    @Autowired
    RequestServiceImpl requestService;

    public abstract List<RequestDto> convertList(Collection<Request> requests);

    @Mapping(source = "version", target = "versionNumber")
    public abstract RequestDto convert(Request request);

    public abstract List<UserLightDto> convertListRequesters(List<User> users);

    @AfterMapping
    void updateIsDeletable(final Request request, @MappingTarget final RequestDto dto) {
        dto.setIsDeletable(requestService.checkIsDeletable(request).isEmpty());
    }
}
