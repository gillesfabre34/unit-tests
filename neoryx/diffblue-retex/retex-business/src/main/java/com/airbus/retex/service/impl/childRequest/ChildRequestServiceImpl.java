package com.airbus.retex.service.impl.childRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.aircraft.AircraftFamilyLightDto;
import com.airbus.retex.business.dto.aircraft.AircraftTypeLightDto;
import com.airbus.retex.business.dto.aircraft.AircraftVersionLightDto;
import com.airbus.retex.business.dto.childRequest.ChildRequestDetailDto;
import com.airbus.retex.business.dto.childRequest.ChildRequestFilteringDto;
import com.airbus.retex.business.dto.childRequest.ChildRequestFullDetailDto;
import com.airbus.retex.business.dto.childRequest.ChildRequestFullDto;
import com.airbus.retex.business.dto.childRequest.ChildRequestLightDto;
import com.airbus.retex.business.dto.client.ClientLightDto;
import com.airbus.retex.business.dto.environment.EnvironmentLightDto;
import com.airbus.retex.business.dto.media.MediaDto;
import com.airbus.retex.business.dto.mission.MissionTypeLightDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.model.admin.aircraft.AircraftFamily;
import com.airbus.retex.model.admin.aircraft.AircraftType;
import com.airbus.retex.model.admin.aircraft.AircraftVersion;
import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.basic.AncestorContext;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.client.Client;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.environment.Environment;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.mission.MissionType;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.part.PartDesignation;
import com.airbus.retex.model.part.PartDesignationFieldsEnum;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routing.RoutingFieldsEnum;
import com.airbus.retex.persistence.aircraft.AircraftFamilyRepository;
import com.airbus.retex.persistence.aircraft.AircraftTypeRepository;
import com.airbus.retex.persistence.aircraft.AircraftVersionRepository;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import com.airbus.retex.persistence.childRequest.PhysicalPartRepository;
import com.airbus.retex.persistence.client.ClientRepository;
import com.airbus.retex.persistence.environment.EnvironmentRepository;
import com.airbus.retex.persistence.mission.MissionTypeRepository;
import com.airbus.retex.persistence.part.PartRepository;
import com.airbus.retex.persistence.request.RequestRepository;
import com.airbus.retex.persistence.routing.RoutingRepository;
import com.airbus.retex.service.aircraft.IAircraftService;
import com.airbus.retex.service.childRequest.IChildRequestService;
import com.airbus.retex.service.impl.childRequest.mapper.ChildRequestMapper;
import com.airbus.retex.service.impl.historization.AbstractHistorizationServiceImpl;
import com.airbus.retex.service.impl.request.RequestServiceImpl;
import com.airbus.retex.service.impl.request.mapper.HistorizationChildRequestMapper;
import com.airbus.retex.service.impl.util.ChildRequestUtil;
import com.airbus.retex.service.media.IMediaService;
import com.airbus.retex.service.translate.ITranslateService;
import com.airbus.retex.service.translate.ITranslationService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ChildRequestServiceImpl extends AbstractHistorizationServiceImpl<ChildRequest> implements IChildRequestService {
    private static final String RETEX_ERROR_CHILDREQUEST_NOT_FOUND = "retex.error.childrequest.not.found";

    private static final Long DEFAULT_DRT_TREATED = 0L;

    @Autowired
    private DtoConverter dtoConverter;

    @Autowired
    private ChildRequestRepository childRequestRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private MissionTypeRepository missionTypeRepository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private AircraftFamilyRepository aircraftFamilyRepository;

    @Autowired
    private AircraftTypeRepository aircraftTypeRepository;

    @Autowired
    private AircraftVersionRepository aircraftVersionRepository;

    @Autowired
    private IAircraftService aircraftService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private IMediaService iMediaService;

    @Autowired
    private ITranslateService translateService;

    @Autowired
    private ITranslationService translationService;

    @Autowired
    private PhysicalPartRepository physicalPartRepository;

    @Autowired
    private RoutingRepository routingRepository;

    @Autowired
    private HistorizationChildRequestMapper historizationChildRequestMapper;

    @Autowired
    private RequestServiceImpl requestServiceImpl;

    @Autowired
    private ChildRequestMapper childRequestMapper;

    private static final Set<EnumStatus> enumStatusForCreation = new HashSet<>(Arrays.asList(EnumStatus.CREATED, EnumStatus.VALIDATED));

    @Override
    public ResponseEntity createChildRequest(Long parentRequestId, ChildRequestDetailDto childRequestCreateDto) throws FunctionalException {
        Request request = requestRepository.findById(parentRequestId).orElseThrow(() ->
                new FunctionalException("retex.error.request.not.found")
        );

        ChildRequest childRequest = dtoConverter.toEntity(childRequestCreateDto, ChildRequest::new);

        if (!childRequestCreateDto.getStatus().equals(EnumStatus.VALIDATED) && !childRequestCreateDto.getStatus().equals(EnumStatus.CREATED)) {
            throw new FunctionalException("retex.error.child.request.status.not.allowed");
        }
        childRequest.setStatus(null == childRequestCreateDto.getStatus() ? EnumStatus.CREATED : childRequestCreateDto.getStatus());

        childRequest.setParentRequest(request);
        setMissionTypeById(childRequestCreateDto.getMissionTypeId(), childRequest);
        setEnvironmentById(childRequestCreateDto.getEnvironmentId(), childRequest);
        setRoutingByPartId(childRequestCreateDto.getPartId(), childRequest);
        setAircraftFamily(childRequestCreateDto.getAircraftFamilyId(), childRequest);
        setAircraftTypes(childRequestCreateDto.getAircraftTypeIds(), childRequest);
        setAircraftVersions(childRequestCreateDto.getAircraftVersionIds(), childRequest);
        validateAircraftInfo(childRequest);
        setMediaUUID(childRequestCreateDto.getMedias(), childRequest);
        setClients(childRequestCreateDto.getClientIds(), childRequest);
        setStatus(childRequestCreateDto.getStatus(), childRequest);

        childRequest.setVersion(1L);
        childRequest = safeSave(childRequest, false, false);

        request.addChildRequest(childRequest);
        requestServiceImpl.safeSave(request, false, false);

        setSerialNumbers(childRequestCreateDto.getSerialNumbers(), childRequest);
        return ResponseEntity.ok(constructChildRequestDetails(childRequest));
    }

    private void setStatus(EnumStatus status, ChildRequest childRequest) throws FunctionalException {
        if (enumStatusForCreation.contains(status)) {
            childRequest.setStatus(status);
        } else {
            throw new FunctionalException("retex.error.childrequest.status.not.allowed");
        }
    }

    /**
     * Returns the ChildRequest instance of raise an exception.
     *
     * @param childRequestID the child request
     * @return the ChildRequest instance
     * @throws NotFoundException if the child request is not found
     */
    private ChildRequest getChildRequestFromId(final Long childRequestID) throws NotFoundException {
        ChildRequest childRequest = childRequestRepository.findById(childRequestID).orElseThrow(
                () -> new NotFoundException(RETEX_ERROR_CHILDREQUEST_NOT_FOUND));

        if (childRequest.getParentRequest() == null) {
            throw new NotFoundException("retex.error.childrequest.is.orphan");
        }

        return childRequest;
    }

    /**
     * @see #updateChildRequest(Long, ChildRequestDetailDto)
     */
    @Override
    public ChildRequestDetailDto updateChildRequest(final Long childRequestId, final ChildRequestDetailDto updateChildRequestDto) throws FunctionalException {

        ChildRequest childRequest = getChildRequestFromId(childRequestId);
        dtoConverter.updateEntity(updateChildRequestDto, childRequest);
        setMissionTypeById(updateChildRequestDto.getMissionTypeId(), childRequest);
        setEnvironmentById(updateChildRequestDto.getEnvironmentId(), childRequest);
        setRoutingByPartId(updateChildRequestDto.getPartId(), childRequest);

        setAircraftFamily(updateChildRequestDto.getAircraftFamilyId(), childRequest);
        setAircraftTypes(updateChildRequestDto.getAircraftTypeIds(), childRequest);
        setAircraftVersions(updateChildRequestDto.getAircraftVersionIds(), childRequest);
        validateAircraftInfo(childRequest);

        setMediaUUID(updateChildRequestDto.getMedias(), childRequest);

        setClients(updateChildRequestDto.getClientIds(), childRequest);
        setSerialNumbers(updateChildRequestDto.getSerialNumbers(), childRequest);

        EnumStatus updateStatusVal = updateChildRequestDto.getStatus();

        if (null == updateStatusVal && !childRequest.getStatus().equals(EnumStatus.VALIDATED)) {
            updateStatusVal = EnumStatus.CREATED;
        }

        if (!EnumStatus.VALIDATED.equals(updateStatusVal) && !EnumStatus.CREATED.equals(updateStatusVal)) {
            throw new FunctionalException("retex.error.child.request.status.not.allowed");
        }
        updateChildRequestDto.setStatus(updateStatusVal);

        boolean increment = childRequest.getStatus().equals(EnumStatus.VALIDATED);
        dtoConverter.updateEntity(updateChildRequestDto, childRequest);

        childRequest = safeSave(childRequest, increment, false);
        return constructChildRequestDetails(childRequest);
    }

    /**
     * Sets the Media.
     *
     * @param mediasUUID   the media UUID
     * @param childRequest the child request instance
     * @throws NotFoundException is raised if the given media is not existing
     */
    private void setMediaUUID(final List<UUID> mediasUUID, final ChildRequest childRequest) throws NotFoundException {
        if (null == mediasUUID || mediasUUID.isEmpty()) {
            return;
        }

        Map<UUID, Media> knownMedia = new HashMap<>();
        for (UUID mediaUUID : mediasUUID) {
            // check if new uuid is in temporary -> moveToPermanentMedia
            Optional<Media> media = iMediaService.temporaryToPermanentMedia(mediaUUID);

            AtomicReference<Media> foundMedia = new AtomicReference<>();
            if (media.isPresent()) {
                childRequest.addMedia(media.get());
                foundMedia.set(media.get());
            } else {
                childRequest.getMedias().forEach(m -> {
                    if (m.getUuid().equals(mediaUUID)) {
                        foundMedia.set(m);
                    }
                });
            }

            if (null == foundMedia.get()) {
                throw new NotFoundException("retex.error.childrequest.mediaUUID.not.found");
            }

            knownMedia.put(foundMedia.get().getUuid(), foundMedia.get());
        }

        List<Media> mediaToDelete = new ArrayList<>();
        childRequest.getMedias().forEach(media -> {
            if (!knownMedia.containsKey(media.getUuid())) {
                mediaToDelete.add(media);
            }
        });
        childRequest.getMedias().removeAll(mediaToDelete);
    }

    private ChildRequestFullDetailDto constructChildRequestDetails(final ChildRequest childRequest) {
        // construct response Dto
        // Lazy binding stuff...manual update

        ChildRequestFullDetailDto response = new ChildRequestFullDetailDto();
        childRequestMapper.toDetailDto(childRequest, response, new AncestorContext());

        response.setIsDeletable(checkIsDeletable(childRequest).isEmpty());

        Optional.ofNullable(childRequest.getEnvironment()).ifPresent(e -> response.setEnvironmentId(e.getId()));
        Optional.ofNullable(childRequest.getMissionType()).ifPresent(m -> response.setMissionTypeId(m.getId()));
        Optional.ofNullable(childRequest.getRouting()).ifPresent(r -> response.setPartId(r.getPart().getNaturalId()));

        Optional.ofNullable(childRequest.getAircraftFamily()).ifPresent(e -> response.setAircraftFamilyId(e.getId()));
        Optional.ofNullable(childRequest.getAircraftTypes())
                .ifPresent(l -> response.setAircraftTypeIds(l.stream().map(AbstractBaseModel::getId).collect(Collectors.toList())));
        Optional.ofNullable(childRequest.getAircraftVersions())
                .ifPresent(l -> response.setAircraftVersionIds(l.stream().map(AbstractBaseModel::getId).collect(Collectors.toList())));

        Optional.ofNullable(childRequest.getMedias())
                .ifPresent(medias -> response.setMedias(medias.stream().map(Media::getUuid).collect(Collectors.toList())));

        Optional.ofNullable(childRequest.getClients())
                .ifPresent(clients -> response.setClientIds(clients.stream().map(AbstractBaseModel::getId).collect(Collectors.toList())));
        Optional.ofNullable(childRequest.getPhysicalParts()).ifPresent(physicalParts -> {
            childRequest.setPhysicalParts(physicalParts);
            response.setSerialNumbers(physicalParts.stream().map(PhysicalPart::getSerialNumber).collect(Collectors.toList()));
        });
               return response;
    }

    @Override
    public ChildRequest updateChildRequestStatus(final Long childRequestId, final EnumStatus newStatus) throws FunctionalException {
        ChildRequest childRequest = getChildRequestFromId(childRequestId);
        childRequest.setStatus(newStatus);
        childRequest = safeSave(childRequest, true, false);
        return childRequest;
    }

    @Override
    public void deleteChildRequest(final Long childRequestId) throws FunctionalException {
        ChildRequest childRequest = getChildRequestFromId(childRequestId);
        Optional<FunctionalException> error = checkIsDeletable(childRequest);
        if (error.isPresent()) {
            throw error.get();
        }

        safeDelete(childRequestId);
    }

    private void validateAircraftInfo(final ChildRequest childRequest) throws FunctionalException {
        Set<Long> childRequestAircraftTypeIds = childRequest.getAircraftTypes().stream().map(AircraftType::getId).collect(Collectors.toSet());
        Set<Long> childRequestAircraftVersionIds = childRequest.getAircraftVersions().stream().map(AircraftVersion::getId).collect(Collectors.toSet());


        if (!aircraftService.findInvalidAircraftTypeIds(childRequest.getAircraftFamily() != null ? childRequest.getAircraftFamily().getId() : null , childRequestAircraftTypeIds).isEmpty()) {
            throw new FunctionalException("retex.error.aircraft.type.incompatible.with.aircraft.family");
        }
        if (!aircraftService.findInvalidAircraftVersionIds(childRequestAircraftTypeIds, childRequestAircraftVersionIds).isEmpty()) {
            throw new FunctionalException("retex.error.aircraft.version.incompatible.with.aircraft.type");
        }

        Request parentRequest = childRequest.getParentRequest();
        if (parentRequest.getAircraftFamily() != null) {
            if (childRequest.getAircraftFamily() == null
                    || !parentRequest.getAircraftFamily().getId().equals(childRequest.getAircraftFamily().getId())) {
                throw new FunctionalException("retex.error.childrequest.aircraft.family.incompatible.with.parent.aircraft.family");
            }
        }
        if (!parentRequest.getAircraftTypes().isEmpty()) {
            if (childRequestAircraftTypeIds.isEmpty() ||
                    !parentRequest.getAircraftTypes().stream().map(AircraftType::getId).collect(Collectors.toSet())
                        .containsAll(childRequestAircraftTypeIds)) {
                 throw new FunctionalException("retex.error.childrequest.aircraft.type.incompatible.with.parent.aircraft.type");
            }
        }
        if (!parentRequest.getAircraftVersions().isEmpty()) {
            if (childRequestAircraftVersionIds.isEmpty() ||
                    !parentRequest.getAircraftVersions().stream().map(AircraftVersion::getId).collect(Collectors.toSet())
                        .containsAll(childRequestAircraftVersionIds)) {
                throw new FunctionalException("retex.error.childrequest.aircraft.version.incompatible.with.parent.aircraft.version");
            }
        }
    }

    /**
     * Sets The aircraft Type. It also verifies that is compatible with parent aircraft type.
     *
     * @param aircraftTypeIds the list of aircraft type Id
     * @param childRequest   the child request to update
     * @throws NotFoundException   if the given aircraftType
     * @throws FunctionalException if the given aircraftType is incompatible with the parent aircraftType
     */
    public void setAircraftTypes(final List<Long> aircraftTypeIds, final ChildRequest childRequest) throws FunctionalException {
        if (aircraftTypeIds != null && !aircraftTypeIds.isEmpty()) {
            for (Long aircraftTypeId : aircraftTypeIds) {
                AircraftType envAircraftType = aircraftTypeRepository.findById(aircraftTypeId)
                        .orElseThrow(() -> new NotFoundException("retex.error.aircraft.type.not.found"));
                childRequest.addAircraftType(envAircraftType);
            }
        } else {
            childRequest.getAircraftTypes().clear();
        }
    }

    /**
     * Sets The aircraft Version. It also verifies that is compatible with parent aircraft version.
     *
     * @param aircraftVersionIds the list of aircraft version id
     * @param childRequest      the child request to update
     * @throws NotFoundException   if the given aircraftType
     * @throws FunctionalException if the given aircraftType is incompatible with the parent aircraftType
     */
    public void setAircraftVersions(final List<Long> aircraftVersionIds, final ChildRequest childRequest) throws FunctionalException {
        if (aircraftVersionIds != null && !aircraftVersionIds.isEmpty()) {
            for (Long aircraftVersionId : aircraftVersionIds) {
                AircraftVersion envAircraftVersion = aircraftVersionRepository.findById(aircraftVersionId)
                        .orElseThrow(() -> new NotFoundException("retex.error.aircraft.version.not.found"));
                childRequest.addAircraftVersion(envAircraftVersion);
            }
        } else {
            childRequest.getAircraftVersions().clear();
        }
    }

    /**
     * Sets The Environment. It also verifies that is compatible with parent environment.
     *
     * @param envId        the environment ID
     * @param childRequest the child request to update
     * @throws NotFoundException   if the given envId is not found
     * @throws FunctionalException if the given envId is incompatible with the parent environment ID
     */
    private void setEnvironmentById(final Long envId, final ChildRequest childRequest) throws FunctionalException {
        if (envId != null) {
            Environment env = environmentRepository.findById(envId).orElseThrow(() -> new NotFoundException("retex.error.childrequest.environment.not.found"));

            Environment parentEnv = childRequest.getParentRequest().getEnvironment();
            if ((parentEnv != null) && !envId.equals(parentEnv.getId())) {
                throw new FunctionalException("retex.error.childrequest.environment.incompatible.with.parent");
            }
            childRequest.setEnvironment(env);
        } else {
            childRequest.setEnvironment(null);
        }
    }

    /**
     * Sets The Mission Type. It also verifies that is compatible with parent mission type.
     *
     * @param missionTypeId the mission type Id
     * @param childRequest  the child request to update
     * @throws NotFoundException   if the given mid is not found
     * @throws FunctionalException if the given mid is incompatible with the parent mission type ID
     */
    private void setMissionTypeById(final Long missionTypeId, final ChildRequest childRequest) throws FunctionalException {
        if (missionTypeId != null) {
            MissionType msType = missionTypeRepository.findById(missionTypeId).orElseThrow(() -> new NotFoundException("retex.error.childrequest.missiontype.not.found"));
            MissionType parentMsType = childRequest.getParentRequest().getMissionType();
            if ((parentMsType != null) && !missionTypeId.equals(parentMsType.getId())) {
                throw new FunctionalException("retex.error.childrequest.missiontype.incompatible.with.parent");
            }
            childRequest.setMissionType(msType);
        } else {
            childRequest.setMissionType(null);
        }
    }

    /**
     * Sets the aircraft family value.
     *
     * @param aircraftFamilyId the given aircraft family id
     * @param childRequest     the child request instance which is necessary to find the parent
     * @throws FunctionalException when there is incompatibilities.
     */
    private void setAircraftFamily(final Long aircraftFamilyId, final ChildRequest childRequest) throws FunctionalException {
        if (aircraftFamilyId != null) {
            AircraftFamily aircraftFamily = aircraftFamilyRepository.findById(aircraftFamilyId)
                    .orElseThrow(() -> new NotFoundException("retex.error.aircraft.family.not.found"));
            childRequest.setAircraftFamily(aircraftFamily);
        } else {
            childRequest.setAircraftFamily(null);
        }
    }

    /**
     * Sets The Part. It also verifies that this part has the ata code compatible with parent ata code.
     *
     * @param partId       the part ID
     * @param childRequest the child request to update
     * @throws NotFoundException   if the given pid is not found
     * @throws FunctionalException if the given pid defining an ata code that is incompatible with the parent ata code
     */
    private void setRoutingByPartId(final Long partId, final ChildRequest childRequest) throws FunctionalException {
        if (partId != null) {
            Part part = partRepository.findValidatedVersionByNaturalId(partId).orElseThrow(() -> new NotFoundException("retex.error.childrequest.part.not.found"));
            String parentAtaCode = childRequest.getParentRequest().getAta().getCode();
            if ((parentAtaCode != null) && ((part.getAta() == null) || (!part.getAta().getCode().equals(parentAtaCode)))) {
                throw new FunctionalException("retex.error.childrequest.part.atacode.incompatible.with.parent.ata.code");
            }
            Routing routing = routingRepository.findByPartNaturalIdAndStatus(part.getNaturalId(), EnumStatus.VALIDATED)
                    .orElseThrow(() -> new FunctionalException("retex.error.routing.not.found"));

            if (!routing.getStatus().equals(EnumStatus.VALIDATED)) {
                throw new FunctionalException("retex.error.childrequest.routing.not.validated");
            }

            childRequest.setRoutingNaturalId(routing.getNaturalId());
            childRequest.setRouting(routing);
        }
    }

    /**
     * Validates the list of given Client ID.
     *
     * @param clientIds the list of client
     * @throws NotFoundException if the given client id list contains invalid client IqD
     */
    private void validateClients(final List<Long> clientIds) throws NotFoundException {

        if (clientIds != null && clientRepository.countByIdIn(clientIds) != clientIds.size()) {
            throw new NotFoundException("retex.error.childrequest.client.not.found");
        }
    }

    /**
     * Sets The SerialNumbers. It also verifies that the given list of serial numbers are valid ids.
     *
     * @param serialNumbers the serial numbers
     * @param childRequest  the child request to update
     * @throws NotFoundException   if any given client is not found
     * @throws FunctionalException if the client list is not a subset of the parent request
     */
    private void setSerialNumbers(List<String> serialNumbers, final ChildRequest childRequest) throws FunctionalException {
        serialNumbers = validateSerialNumbers(serialNumbers, childRequest);
        List<Long> crSerialNumbers = updateSerialNumbers(childRequest, serialNumbers);
        dtoConverter.mapIdsAddRemove(crSerialNumbers, childRequest.getPhysicalParts(), PhysicalPart.class);
    }

    /**
     * Retrieves the serial number entity from the value of serial number (the serial number value SN1 can be associated to Child Request 1 and Child Request 2.)
     * It is why retrieving it , it is required to use the child request ID as well :)
     * If the serial number is not existing creates one.
     *
     * @param childRequest  the child request
     * @param serialNumbers the serial numbers
     * @return the list of Serial Number Ids.
     */
    private List<Long> updateSerialNumbers(final ChildRequest childRequest, final List<String> serialNumbers) throws FunctionalException {
        if (CollectionUtils.isEmpty(serialNumbers)) {
            throw new FunctionalException("retex.error.childrequest.serialnumber.not.found");
        }
        List<PhysicalPart> physicalPartsToRemove = physicalPartRepository.findByChildRequestAndSerialNumberNotIn(childRequest, serialNumbers);
        physicalPartRepository.deleteInBatch(physicalPartsToRemove);

        List<Long> physicalPartIds = new ArrayList<>();
        for (String serialNumber : serialNumbers) {
            if (!ChildRequestUtil.isSerialNumberValid(serialNumber)) {
                throw new FunctionalException("retex.error.childrequest.serialnumber.not.valid");
            }
            PhysicalPart physicalPart = new PhysicalPart();
            Optional<PhysicalPart> optionalPhysicalPart = physicalPartRepository.findByChildRequestAndSerialNumber(childRequest, serialNumber);
            if (optionalPhysicalPart.isPresent()) {
                physicalPart = optionalPhysicalPart.get();
            } else {
                physicalPart.setChildRequest(childRequest);
                physicalPart.setSerialNumber(serialNumber);
                physicalPart.setPart(childRequest.getRouting().getPart());
                physicalPartRepository.save(physicalPart);
            }
            physicalPartIds.add(physicalPart.getId());
        }
        return physicalPartIds;
    }


    /**
     * Validates that the list of Serial number does not contain fouble entries.
     *
     * @param serialNumbers the serial numbers
     * @throws FunctionalException raised if duplicate found
     */
    private List<String> validateSerialNumbers(final List<String> serialNumbers, final ChildRequest childRequest) throws FunctionalException {
        List<String> tempSetForSerialNumbers = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(serialNumbers)) {
            for (String serialNumber : serialNumbers) {
                if (physicalPartRepository.existsBySerialNumberAndPartAndChildRequestNot(serialNumber, childRequest.getRouting().getPart(), childRequest)) {
                    throw new FunctionalException("retex.error.childrequest.couple.partNumber.serialNumber.exists", new Object[]{childRequest.getRouting().getPart().getPartNumber(), serialNumber});
                }
            }

            serialNumbers.stream()
                    // get serial number and not duplicates
                    .filter(snValue -> !(snValue.trim().isEmpty()) && !tempSetForSerialNumbers.add(snValue))
                    .collect(Collectors.toSet());
        }
        return tempSetForSerialNumbers;
    }

    /**
     * Sets The Clients. It also verifies that the given list of clients if not empty is a sub list of Parent request.
     *
     * @param clientIds    the clients
     * @param childRequest the child request to update
     * @throws NotFoundException   if any given client is not found
     * @throws FunctionalException if the client list is not a subset of the parent request
     */
    private void setClients(final List<Long> clientIds, final ChildRequest childRequest) throws FunctionalException {

        if ((clientIds != null) && (!clientIds.isEmpty())) {
            validateClients(clientIds);
            // Parent request , no client ... child can have any valid clients...
            Set<Client> parentRequestClients = childRequest.getParentRequest().getClients();
            if ((parentRequestClients != null) && (!parentRequestClients.isEmpty())) {
                Set<Long> parentClientIds = parentRequestClients.stream().map(AbstractBaseModel::getId).collect(Collectors.toSet());
                if (!parentClientIds.containsAll(clientIds)) {
                    throw new FunctionalException("retex.error.childrequest.clients.are.not.subset.of.parent.request");
                }

            }
            dtoConverter.mapIdsAddRemove(clientIds, childRequest.getClients(), Client.class);
        } else {
            dtoConverter.mapIdsAddRemove(new ArrayList<>(), childRequest.getClients(), Client.class);
        }
    }

    /**
     * @see #findFilteredChildRequestOfRequest(ChildRequestFilteringDto, Long, Language)
     */
    @Override
    public PageDto<ChildRequestLightDto> findFilteredChildRequestOfRequest(ChildRequestFilteringDto childRequestFilteringDto, Long requestId, Language language) {
        Sort sort = findChildRequestsWithFiltersSort(childRequestFilteringDto);
        Pageable pageable = PageRequest.of(childRequestFilteringDto.getPage(), childRequestFilteringDto.getSize(), sort);
        Page<ChildRequest> requests = childRequestRepository.findAll(requestId, pageable);
        return constructListLightChildRequests(requests, language);
    }

    private PageDto<ChildRequestLightDto> constructListLightChildRequests(Page<ChildRequest> childRequests, Language language) {
        List<ChildRequestLightDto> childRequestLightDtos = new ArrayList<>();
        Map<Long, Boolean> map = childRequests.stream().collect(
                Collectors.toMap(ChildRequest::getId, childRequest -> checkIsDeletable(childRequest).isEmpty())
        );
        childRequests.getContent().forEach(cr -> {
            ChildRequestLightDto childRequestLightDto = new ChildRequestLightDto();
            childRequestLightDto.setId(cr.getId());
            childRequestLightDto.setStatus(cr.getStatus());
            childRequestLightDto.setVersionNumber(cr.getVersion());
            childRequestLightDto.setPartNumber(cr.getRouting().getPart().getPartNumber());
            childRequestLightDto.setPartNumberRoot(cr.getRouting().getPart().getPartNumberRoot());
            childRequestLightDto.setDrtTotal(cr.getDrtToInspect());
            childRequestLightDto.setDrtTreated(DEFAULT_DRT_TREATED);// for moment : default value is 0
            childRequestLightDto.setDesignation(translateService.getFieldValue(PartDesignation.class.getSimpleName(),
                    cr.getRouting().getPart().getPartDesignation().getId(), PartDesignationFieldsEnum.designation.name(), language));
            childRequestLightDto.setIsDeletable(map.get(cr.getId()));
            childRequestLightDtos.add(childRequestLightDto);
        });
        return new PageDto<>(childRequestLightDtos, childRequests.getTotalElements(), childRequests.getTotalPages());
    }

    private Sort findChildRequestsWithFiltersSort(final ChildRequestFilteringDto childRequestFilteringDto) {
        if (childRequestFilteringDto.getSortBy() == null) {
            return Sort.unsorted();
        }
        Sort.Direction direction = childRequestFilteringDto.getSortDirection() == null ? Sort.Direction.ASC : childRequestFilteringDto.getSortDirection();
        return Sort.by(direction, childRequestFilteringDto.getSortBy().label);
    }

    @Override
    public ChildRequestFullDto getChildRequest(final Long id, final Long version, final Language language) throws FunctionalException {
        ChildRequest childRequest;
        if (version == null) {
            childRequest = childRequestRepository.findById(id)
                    .orElseThrow(() -> new FunctionalException(RETEX_ERROR_CHILDREQUEST_NOT_FOUND));
        } else {
            childRequest = getSpecificVersion(ChildRequest.class, id, version, null)
                    .orElseThrow(() -> new NotFoundException(RETEX_ERROR_CHILDREQUEST_NOT_FOUND));
        }

        return buildChildRequestResponse(childRequest, language);
    }

    @Override
    public Optional<ChildRequest> getSpecificVersion(final Class<ChildRequest> cls, final Long id, final Long version, final LocalDateTime localDateTime) throws FunctionalException {
        Optional<ChildRequest> childRequestOpt = super.getSpecificVersion(cls, id, version, localDateTime);

        // JoinFormula does not seems to work on a specific audit version, so we get the routing manually
        if (childRequestOpt.isPresent()) {
            Optional<Routing> routingOpt = routingRepository.findValidatedVersionByNaturalId(childRequestOpt.get().getRoutingNaturalId());
            childRequestOpt.get().setRouting(routingOpt.orElse(null));
        }

        return childRequestOpt;
    }

    private ChildRequestFullDto buildChildRequestResponse(ChildRequest childRequest, Language language) throws FunctionalException {
        ChildRequestFullDto response = dtoConverter.toDto(childRequest, ChildRequestFullDto.class);
        response.setVersionNumber(childRequest.getVersion());
        // set clients
        List<ClientLightDto> parentClients = dtoConverter.toDtos(childRequest.getParentRequest().getClients(), ClientLightDto.class);
        List<ClientLightDto> childClients = dtoConverter.toDtos(childRequest.getClients(), ClientLightDto.class);
        ensureChildClientsIsSublistOfParentChilds(parentClients, childClients);

        response.setChildRequestClients(new HashSet<>(childClients));
        parentClients.removeAll(childClients);
        response.setParentRequestClients(new HashSet<>(parentClients));

        // set air craft information
        response.setAircraftFamily(dtoConverter.toDto(childRequest.getAircraftFamily(), AircraftFamilyLightDto.class));
        response.setAircraftTypes(dtoConverter.toDtos(childRequest.getAircraftTypes(), AircraftTypeLightDto.class));
        response.setAircraftVersions(dtoConverter.toDtos(childRequest.getAircraftVersions(), AircraftVersionLightDto.class));
        response.setMissionType(dtoConverter.toDto(childRequest.getMissionType(), MissionTypeLightDto.class));

        response.setAtaCode(childRequest.getRouting().getPart().getAta().getCode());
        response.setEnvironment(dtoConverter.toDto(childRequest.getEnvironment(), EnvironmentLightDto.class));

        List<MediaDto> mediaDtos = dtoConverter.toDtos(childRequest.getMedias(), MediaDto.class);
        response.setMedias(new HashSet<>(mediaDtos));

        response.setModulation(childRequest.getModulation());
        response.setRouting(translationService.getFieldValue(childRequest.getRouting(), RoutingFieldsEnum.name, language));
        PartDesignation partDesignation = childRequest.getRouting().getPart().getPartDesignation();
        response.setDesignation(translateService.getFieldValue(PartDesignation.class.getSimpleName(),
                partDesignation.getId(), PartDesignationFieldsEnum.designation.name(), language));
        response.setPartNumber(childRequest.getRouting().getPart().getPartNumber());
        response.setPartNumberRoot(childRequest.getRouting().getPart().getPartNumberRoot());
        response.setDrtTotal(childRequest.getDrtToInspect());
        response.setPartId(childRequest.getRouting().getPart().getNaturalId());
        // TODO : implémenter plus tard, pour calculer le nombre de drt restant a traiter
        response.setDrtTreated(DEFAULT_DRT_TREATED);
        response.setSerialNumbers(childRequest.getPhysicalParts().stream().map(PhysicalPart::getSerialNumber).collect(Collectors.toSet()));
        response.setIsDeletable(checkIsDeletable(childRequest).isEmpty());
        return response;
    }

    /**
     * ChildRequest's clients should be a sub-list (or equal) of Parent Request
     *
     * @param parentClients
     * @param childClients
     * @throws FunctionalException
     */
    private void ensureChildClientsIsSublistOfParentChilds(List<ClientLightDto> parentClients, List<ClientLightDto> childClients) throws FunctionalException {
        boolean exceed = childClients.size() > parentClients.size();
        List<Long> parentsClientsIds = parentClients.stream().map(ClientLightDto::getId).collect(Collectors.toList());
        List<Long> childClientsIds = childClients.stream().map(ClientLightDto::getId).collect(Collectors.toList());
        if (!parentsClientsIds.containsAll(childClientsIds) || exceed) {
            throw new FunctionalException("retex.error.childrequest.clients.are.not.subset.of.parent.request");
        }
    }

    @Override
    public void copyFrom(final ChildRequest source, final ChildRequest target) {
        historizationChildRequestMapper.copyFrom(source, target);
    }

    @Override
    public Optional<FunctionalException> checkIsDeletable(final ChildRequest childRequest) {
        Optional<FunctionalException> error = Optional.empty();

        if (childRequest == null) {
            error = Optional.of(new FunctionalException("retex.error.childrequest.is.null"));
        }

        else if (error.isEmpty() && (childRequest.getStatus() != EnumStatus.CREATED)
                && (childRequest.getStatus() != EnumStatus.VALIDATED)) {
            error = Optional.of(new FunctionalException("retex.error.childrequest.invalid.status.delete.impossible"));
        }

        else if (error.isEmpty() && !childRequest.getDrts().isEmpty()) {
            error = Optional.of(new FunctionalException("retex.error.childrequest.has.drt.delete.impossible"));
        }

        return error;
    }

    @Override
    public void repositoryDelete(final ChildRequest childRequest) throws FunctionalException {
        Request parentRequest = childRequest.getParentRequest();
        if (parentRequest == null) {
            throw new FunctionalException("retex.error.childrequest.is.orphan");
        }
        parentRequest.removeChildRequest(childRequest);
        requestServiceImpl.safeSave(parentRequest, true, false);
        childRequestRepository.delete(childRequest);
    }
}
