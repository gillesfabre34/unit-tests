package com.airbus.retex.service.impl.request;

import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.request.*;
import com.airbus.retex.business.dto.user.UserLightDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.model.admin.airbus.AirbusEntity;
import com.airbus.retex.model.admin.aircraft.AircraftType;
import com.airbus.retex.model.admin.aircraft.AircraftVersion;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.request.RequestFieldsEnum;
import com.airbus.retex.model.request.Specification.RequestSpecification;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.airbus.AirbusEntityRepository;
import com.airbus.retex.persistence.request.RequestRepository;
import com.airbus.retex.service.aircraft.IAircraftService;
import com.airbus.retex.service.impl.childRequest.ChildRequestServiceImpl;
import com.airbus.retex.service.impl.historization.AbstractHistorizationServiceImpl;
import com.airbus.retex.service.impl.request.mapper.*;
import com.airbus.retex.service.media.IMediaService;
import com.airbus.retex.service.request.IRequestService;
import com.airbus.retex.service.translate.ITranslateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class RequestServiceImpl extends AbstractHistorizationServiceImpl<Request> implements IRequestService {
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private AirbusEntityRepository airbusEntityRepository;
    @Autowired
    private ITranslateService translateService;
    @Autowired
    private IMediaService iMediaService;
    @Autowired
    private RequestDetailsDtoMapper requestDetailsDtoMapper;
    @Autowired
    private RequestDtoMapper requestDtoMapper;
    @Autowired
    private RequestFullDtoMapper requestFullDtoMapper;
    @Autowired
    private RequestMapper requestMapper;
    @Autowired
    private IAircraftService aircraftService;
    @Autowired
	private HistorizationRequestMapper historizationRequestMapper;
    @Autowired
	private ChildRequestServiceImpl childRequestService;

    @Override
    public PageDto<RequestDto> findRequestsWithFilters(RequestFilteringDto requestFiltering, Language lang) {
        Sort sort = this.getFindRequestsWithFiltersSort(requestFiltering);
        Pageable pageable = PageRequest.of(requestFiltering.getPage(), requestFiltering.getSize(), sort);

        Page<Request> requests = requestRepository.findAll(buildSpecification(requestFiltering, lang), pageable);

        List<RequestDto> requestDtos = requestDtoMapper.convertList(requests.getContent());

        return new PageDto<>((requestDtos), requests.getTotalElements(), requests.getTotalPages());
    }

    @Override
    public List<UserLightDto> getAllRequesters() {
        List<User> result = requestRepository.getAllRequesters();
        return requestDtoMapper.convertListRequesters(result);
    }

    @Override
    public RequestFullDto findRequestById(final Long id, final Long version) throws FunctionalException {
        Request request = null;
        if (version == null) {
            request = getRequestById(id);
        } else {
            request = getSpecificVersion(Request.class, id, version, null)
                    .orElseThrow(() -> new NotFoundException("retex.error.request.not.found"));
        }
        RequestFullDto requestFullDto = requestFullDtoMapper.convert(request);

        // RequestFullDto contains translation fields (name & description)....so we need to use translateService here.
        Map<Language, Map<RequestFieldsEnum, String>> requestFields =
                translateService.getTranslatedFields(request, EnumSet.allOf(RequestFieldsEnum.class));
        requestFullDto.setTranslatedFields(requestFields);
        return requestFullDto;
    }

    @Override
    public RequestDetailsDto updateRequestDetail(Long id, @Valid RequestUpdateDto requestUpdateDto, boolean validated) throws FunctionalException {
        Request request = getRequestById(id);

        verifyRequestAndChildRequestDtoRelationShips(request, requestUpdateDto);
        verifyAircraftInfo(requestUpdateDto);

        BiFunction<Set<Media>, Set<Media>, Set> cleanMedias = (newMedias, currentMedias) -> {
            Map<UUID, Media> map = newMedias.stream().collect(Collectors.toMap(Media::getUuid, m -> m));
            List<Media> mediaToDelete = new ArrayList<>();

            currentMedias.forEach(media -> {
                if (!map.containsKey(media.getUuid())) {
                    mediaToDelete.add(media);
                }
            });
            currentMedias.removeAll(mediaToDelete);

            return currentMedias;
        };

        if (requestUpdateDto.getOriginMedias() != null) {
            Set<Media> originMedia = iMediaService.getAsPermanentMedias(requestUpdateDto.getOriginMedias());
            request.getOriginMedias().addAll(originMedia);
            cleanMedias.apply(originMedia, request.getOriginMedias());
        }

        if (requestUpdateDto.getSpecMedias() != null) {
            Set<Media> specMedia = iMediaService.getAsPermanentMedias(requestUpdateDto.getSpecMedias());
            request.getSpecMedias().addAll(specMedia);
            cleanMedias.apply(specMedia, request.getSpecMedias());
        }

        // once the request is validated we always increment version
        boolean increment = request.getStatus().equals(EnumStatus.VALIDATED);

        if (validated) {
            request.setStatus(EnumStatus.VALIDATED);
        }

        requestMapper.update(requestUpdateDto, request);

        request = safeSave(request, increment, false);
        return requestDetailsDtoMapper.convert(request);
    }

    private void verifyAircraftInfo(RequestUpdateDto requestUpdateDto) throws FunctionalException {
        Long aircraftFamilyId = requestUpdateDto.getAircraftFamily()!= null ? requestUpdateDto.getAircraftFamily().getId() : null;
        List<Long> childRequestAircraftTypeIds = requestUpdateDto.getAircraftTypes();
        List<Long> childRequestAircraftVersionIds = requestUpdateDto.getAircraftVersions();
        if (!aircraftService.findInvalidAircraftTypeIds(aircraftFamilyId, childRequestAircraftTypeIds).isEmpty()) {
            throw new FunctionalException("retex.error.aircraft.type.incompatible.with.aircraft.family");
        }
        if (!aircraftService.findInvalidAircraftVersionIds(childRequestAircraftTypeIds, childRequestAircraftVersionIds).isEmpty()) {
            throw new FunctionalException("retex.error.aircraft.version.incompatible.with.aircraft.type");
        }
    }

    /**
     * Check if setting environment and mission type could invalidate children requests
     * @param request
     * @param requestUpdateDto
     * @throws FunctionalException
     */
    private void verifyRequestAndChildRequestDtoRelationShips(Request request, RequestUpdateDto requestUpdateDto) throws FunctionalException {
        if (request.getChildRequests().isEmpty()) {
            return;
        }

        Set<Long> aircraftTypes = requestUpdateDto.getAircraftTypes().stream().collect(Collectors.toSet());
        Set<Long> aircraftVersions = requestUpdateDto.getAircraftVersions().stream().collect(Collectors.toSet());

        for (ChildRequest childRequest : request.getChildRequests()) {
            if (requestUpdateDto.getAircraftFamily() != null && requestUpdateDto.getAircraftFamily().getId() != null &&
                    ((childRequest.getAircraftFamily() == null) || (!childRequest.getAircraftFamily().getId().equals(requestUpdateDto.getAircraftFamily().getId())))) {
                throw new FunctionalException("retex.error.request.aircraft.family.invalidate.children");
            }
            if (!aircraftTypes.isEmpty()) {
                if(childRequest.getAircraftTypes().isEmpty() ||
                        !aircraftTypes.containsAll(childRequest.getAircraftTypes().stream().map(AircraftType::getId).collect(Collectors.toSet()))) {
                    throw new FunctionalException("retex.error.request.aircraft.type.invalidate.children");
                }
            }
            if (!aircraftVersions.isEmpty()) {
                if (childRequest.getAircraftVersions().isEmpty() ||
                        !aircraftVersions.containsAll(childRequest.getAircraftVersions().stream().map(AircraftVersion::getId).collect(Collectors.toSet()))) {
                    throw new FunctionalException("retex.error.request.aircraft.version.invalidate.children");
                }
            }
            if (requestUpdateDto.getEnvironment() != null && requestUpdateDto.getEnvironment().getId() != null &&
                    (!childRequest.getEnvironment().getId().equals(requestUpdateDto.getEnvironment().getId()))) {
                throw new FunctionalException("retex.error.request.environment.invalidate.children");
            }
            if (requestUpdateDto.getMissionType() != null && requestUpdateDto.getMissionType().getId() != null &&
                    (!childRequest.getMissionType().getId().equals(requestUpdateDto.getMissionType().getId()))) {
                throw new FunctionalException("retex.error.request.missionType.invalidate.children");
            }
        }
    }

    @Override
    public void deleteRequest(final Long id) throws FunctionalException {
        Request request = getRequestById(id);
        Optional<FunctionalException> error = checkIsDeletable(request);
        if (error.isPresent()) {
            throw error.get();
        }
        safeDelete(request.getId());
    }

    @Override
    public void updateRequestStatus(final Long id, final EnumStatus newStatus) throws FunctionalException {
        Request request = getRequestById(id);
        Set<ChildRequest> childRequests = request.getChildRequests();

        for (ChildRequest childRequest : childRequests) {
            if (!childRequest.getStatus().equals(EnumStatus.COMPLETED) || !childRequest.getStatus().equals(EnumStatus.DELETED)) {
                throw new FunctionalException("retex.error.close.request");
            }
        }

        request.setStatus(EnumStatus.CLOSED);
        safeSave(request, true, false);
    }

    private Sort getFindRequestsWithFiltersSort(RequestFilteringDto requestFiltering) {
        if (requestFiltering.getSortBy() == null) {
            // Unsorted by default
            return Sort.unsorted();
        }
        // Default sort direction ASC or use the provided value
        Sort.Direction direction = requestFiltering.getSortDirection() == null ? Sort.Direction.ASC : requestFiltering.getSortDirection();
		return Sort.by(direction, requestFiltering.getSortBy().label);
    }

    @Override
    public RequestDto createRequest(@Valid RequestCreationDto dto, User requester) throws FunctionalException {
        Request item = new Request();
        requestMapper.create(dto, item);
        Optional<AirbusEntity> airbusEntity = airbusEntityRepository.findById(dto.getAirbusEntityId());
        if (airbusEntity.isEmpty()) {
            throw new NotFoundException("retex.airbusEntity.notExists");
        }

        item.setCreationDate(LocalDateTime.now());
        item.setRequester(requester);
        item.setStatus(EnumStatus.CREATED);
        item.setVersion(1L);
        return saveRequest(item, dto.getTranslatedFields());
    }

    @Override
    public RequestDto updateRequest(final Long id, final RequestCreationDto dto) throws FunctionalException {
        Request item = getRequestById(id);
        requestMapper.updateLight(dto, item);

        AirbusEntity airbusEntity = airbusEntityRepository.findById(dto.getAirbusEntityId())
                .orElseThrow(() -> new NotFoundException("retex.airbusEntity.notExists"));

        item.setAirbusEntity(airbusEntity);
        return saveRequest(item, dto.getTranslatedFields());
    }

    private RequestDto saveRequest(Request item, @NotNull Map<Language, Map<RequestFieldsEnum, String>> translatedFields) throws FunctionalException{
        item = safeSave(item, true, true);
        requestRepository.detach(item);
        translateService.saveTranslatedFields(item, translatedFields);
        Request request = requestRepository.getOne(item.getId());

        return requestDtoMapper.convert(request);
    }

    private Specification<Request> buildSpecification(RequestFilteringDto requestFiltering, Language lang) {
        Specification<Request> specification = Specification.where(null);
        if (null != requestFiltering.getName()) {
            specification = specification.and(RequestSpecification.filterByName(requestFiltering.getName(), lang));
        } else {
            specification = specification.and(RequestSpecification.filterByTranslationOnName(lang));
        }
        if (null != requestFiltering.getAirbusEntityIds()) {
            Specification<Request> spec = Specification.where(null);
            for (Long aid : requestFiltering.getAirbusEntityIds()) {
                spec = spec.or(RequestSpecification.filterByEntity(aid));
            }
            specification = specification.and(spec);
        }
        if (null != requestFiltering.getRequesterIds()) {
            Specification<Request> spec = Specification.where(null);
            for (Long requester : requestFiltering.getRequesterIds()) {
                spec = spec.or(RequestSpecification.filterByRequester(requester));
            }
            specification = specification.and(spec);
        }
        if (null != requestFiltering.getOriginIds()) {
            Specification<Request> spec = Specification.where(null);
            for (Long origin : requestFiltering.getOriginIds()) {
                spec = spec.or(RequestSpecification.filterByOrigin(origin));
            }
            specification = specification.and(spec);
        }
        if (null != requestFiltering.getStatuses()) {
            Specification<Request> spec = Specification.where(null);
            for (EnumStatus status : requestFiltering.getStatuses()) {
                spec = spec.or(RequestSpecification.filterByStatus(status));
            }
            specification = specification.and(spec);
        }
        return specification;
    }


    @Override
    public List<String> getTechnicalManagersOfRequest(Long id) throws FunctionalException {
        Request request = getRequestById(id);
        return request.getTechnicalResponsibles().stream().map(user -> user.getFirstName() + " " + user.getLastName()).collect(Collectors.toList());
    }


    @Override
    public void copyFrom(final Request source, final Request target) {
        historizationRequestMapper.copyFrom(source,target);
    }

    @Override
    public Optional<FunctionalException> checkIsDeletable(final Request request) {
        Optional<FunctionalException> error = Optional.empty();

        if (request == null) {
            return Optional.of(new FunctionalException("retex.error.request.is.null"));
        }

		if (error.isEmpty()
				&& (request.getStatus() != EnumStatus.CREATED && (request.getStatus() != EnumStatus.VALIDATED))) {
            error = Optional.of(new FunctionalException("retex.error.request.invalid.status.delete.impossible"));
        }

        if (error.isEmpty()) {
            // if one of the child request is not deletablen, it raises an exception and we go out of the method any way
            // so don't need to verify the return value here of the chek function
            for (ChildRequest childRequest : request.getChildRequests()) {
                if (error.isEmpty()) {
                    error = childRequestService.checkIsDeletable(childRequest);
                }
            }
        }

        return error;
    }

    /**
     * Returns the ChildRequest instance of raise an exception.
     * @param requestID the request id
     * @return the Request instance
     * @throws NotFoundException if the request is not found
     */
    private Request getRequestById(final Long requestID)throws NotFoundException {
        return requestRepository.findById(requestID).orElseThrow(
                () -> new NotFoundException("retex.error.request.not.found"));
    }
}
