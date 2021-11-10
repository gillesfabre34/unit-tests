package com.airbus.retex.service.impl.request.mapper;

import com.airbus.retex.business.dto.request.RequestCreationDto;
import com.airbus.retex.business.dto.request.RequestUpdateDto;
import com.airbus.retex.business.mapper.AbstractMapper;
import com.airbus.retex.business.mapper.ReferenceMapper;

import com.airbus.retex.model.admin.aircraft.AircraftType;
import com.airbus.retex.model.admin.aircraft.AircraftVersion;
import com.airbus.retex.model.client.Client;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.user.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.airbus.retex.business.mapper.MapperUtils.updateList;

@Mapper(componentModel = "spring", uses = ReferenceMapper.class)
public abstract class RequestMapper extends AbstractMapper {

    @Autowired
    private ReferenceMapper referenceMapper;

    @Mapping(source = "ataCode", target = "ata")
    @Mapping(source = "originId", target = "origin")
    @Mapping(source = "operatorIds", target = "operators")
    @Mapping(source = "technicalManagerIds", target = "technicalResponsibles")
    @Mapping(target = "aircraftTypes", ignore = true)
    @Mapping(target = "aircraftVersions", ignore = true)
    @Mapping(target = "originMedias", ignore = true)
    @Mapping(target = "specMedias", ignore = true)
    public abstract void update(RequestUpdateDto source, @MappingTarget Request target);

    @Mapping(source = "airbusEntityId", target = "airbusEntity")
    public abstract void create(RequestCreationDto source, @MappingTarget Request target);

    @Mapping(source = "airbusEntityId", target = "airbusEntity")
    public abstract void updateLight(RequestCreationDto source, @MappingTarget Request target);


    @AfterMapping
    public void afterUpdate(RequestUpdateDto source, @MappingTarget Request target) {
        updateClientList(source.getClientIds(), target.getClients());
        updateUserList(source.getTechnicalManagerIds(), target.getTechnicalResponsibles());
        updateUserList(source.getOperatorIds(), target.getOperators());
        updateMediaList(source.getOriginMedias(),target.getOriginMedias());
        updateMediaList(source.getSpecMedias(),target.getSpecMedias());
        updateAircraftTypeList(source.getAircraftTypes(), target.getAircraftTypes());
        updateAircraftVersionList(source.getAircraftVersions(), target.getAircraftVersions());
    }

    public void updateUserList(List<Long> source, @MappingTarget Set<User> target) {
        BiFunction<Long, User, Boolean> isSame = (id, user) -> (id != null && id.equals(user.getId()));
		Function<Long, User> resolveSourceFromDestination = id -> referenceMapper.resolve(id, User.class);
        updateList(source, target, isSame, resolveSourceFromDestination);
    }

    public void updateClientList(List<Long> source, @MappingTarget Set<Client> target) {
        BiFunction<Long, Client, Boolean> isSame = (id, user) -> (id != null && id.equals(user.getId()));
		Function<Long, Client> resolveSourceFromDestination = id -> referenceMapper.resolve(id, Client.class);
        updateList(source, target, isSame, resolveSourceFromDestination);
    }

    public void updateAircraftTypeList(List<Long> source, @MappingTarget Set<AircraftType> target) {
        BiFunction<Long, AircraftType, Boolean> isSame = (id, obj) -> (id != null && id.equals(obj.getId()));
		Function<Long, AircraftType> resolveSourceFromDestination = id -> referenceMapper.resolve(id,
				AircraftType.class);
        updateList(source, target, isSame, resolveSourceFromDestination);
    }

    public void updateAircraftVersionList(List<Long> source, @MappingTarget Set<AircraftVersion> target) {
        BiFunction<Long, AircraftVersion, Boolean> isSame = (id, obj) -> (id != null && id.equals(obj.getId()));
		Function<Long, AircraftVersion> resolveSourceFromDestination = id -> referenceMapper.resolve(id,
				AircraftVersion.class);
        updateList(source, target, isSame, resolveSourceFromDestination);
    }
}
