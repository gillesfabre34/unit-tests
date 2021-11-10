package com.airbus.retex.service.impl.request.mapper;

import com.airbus.retex.business.dto.request.RequestDetailsDto;
import com.airbus.retex.business.mapper.AbstractMapper;
import com.airbus.retex.model.client.Client;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.user.User;
import com.airbus.retex.service.impl.request.RequestServiceImpl;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class RequestDetailsDtoMapper extends AbstractMapper {

    @Autowired
    RequestServiceImpl requestService;

	@Mapping(source = "clients", target = "clientIds")
	@Mapping(source = "origin.id", target = "originId")
	@Mapping(source = "originMedias", target = "originMedias")
	@Mapping(source = "specMedias", target = "specMedias")
	@Mapping(source = "technicalResponsibles", target = "technicalManagerIds")
	@Mapping(source = "operators", target = "operatorIds")
	@Mapping(source = "environment.id", target = "environmentId")
	@Mapping(source = "missionType.id", target = "missionTypeId")
    public abstract RequestDetailsDto convert(Request request);

    protected abstract List<Long> clientsToClientIds(Set<Client> clients);

    protected Long clientToClientId(Client client) {
        return client.getId();
    }

    protected abstract List<Long> userToUserIds(Set<User> users);

    protected Long userToUserId(User user) {
        return user.getId();

    }

    @AfterMapping
    void updateIsDeletable(final Request request, @MappingTarget final RequestDetailsDto dto) {
        dto.setIsDeletable(requestService.checkIsDeletable(request).isEmpty());
    }

    protected abstract List<UUID> mediasToMediaUuids(Set<Media> medias);

    protected UUID mediasToMediaUuid(Media media) {
        return media.getUuid();
    }

}
