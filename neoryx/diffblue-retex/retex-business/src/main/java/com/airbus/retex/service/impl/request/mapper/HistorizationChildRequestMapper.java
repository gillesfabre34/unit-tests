package com.airbus.retex.service.impl.request.mapper;

import com.airbus.retex.business.mapper.AbstractMapper;
import com.airbus.retex.business.mapper.ReferenceMapper;
import com.airbus.retex.model.childrequest.ChildRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = ReferenceMapper.class)
public abstract class HistorizationChildRequestMapper extends AbstractMapper {

    public void copyFrom(ChildRequest source, ChildRequest target) {
        target.setVersion(source.getVersion());
        target.setStatus(source.getStatus());
        target.setDrtToInspect(source.getDrtToInspect());
    }

}

