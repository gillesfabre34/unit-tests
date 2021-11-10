package com.airbus.retex.service.impl.childRequest.mapper;

import com.airbus.retex.business.dto.childRequest.ChildRequestDetailDto;
import com.airbus.retex.business.dto.childRequest.ChildRequestFullDetailDto;
import com.airbus.retex.business.mapper.AbstractMapper;
import com.airbus.retex.business.mapper.ReferenceMapper;
import com.airbus.retex.model.basic.AncestorContext;
import com.airbus.retex.model.childrequest.ChildRequest;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ReferenceMapper.class})
public abstract class ChildRequestMapper extends AbstractMapper {

    public abstract void toDetailDto(ChildRequest childRequest, @MappingTarget ChildRequestDetailDto dto, @Context AncestorContext context);

    public abstract void toFullDetailDto(ChildRequest childRequest, @MappingTarget ChildRequestFullDetailDto dto, @Context AncestorContext context);

}
