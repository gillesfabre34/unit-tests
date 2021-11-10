package com.airbus.retex.service.childRequest;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.childRequest.ChildRequestDetailDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.model.admin.aircraft.AircraftFamily;
import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import com.airbus.retex.persistence.request.RequestRepository;
import com.airbus.retex.persistence.routing.RoutingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
public class ChildRequestUpdateServiceIT extends AbstractServiceIT {

    private Request parentRequest1;
    private Request parentRequest2;

    private ChildRequest childRequest11;

    private ChildRequest orphanChildRequest;

    @Autowired
    private DtoConverter dtoConverter;

    @Autowired
    private IChildRequestService childRequestService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RoutingRepository routingRepository;

    @Autowired
    private ChildRequestRepository childRequestRepository;

    private Validator constraintValidator;
    private ChildRequestDetailDto childRequestCreationDto;

    private static final String INVALID_SERIAL_NUMBER = "__sn_1";
    private static final Integer INVALID_DRT_TO_INSPECT = -5;
    private static final List<String> LIST_SERIAL_NUMBERS = List.of("10001152");
    Request parentRequest;
    ChildRequest childRequest;

    @BeforeEach
    public void before() {
        runInTransaction(() -> {
            dataset.routing_1.setStatus(EnumStatus.VALIDATED);
            routingRepository.save(dataset.routing_1);
            parentRequest1 = dataSetInitializer.createRequest("parentRequest1",
                    request -> {
                        request.setEnvironment(dataset.environment_1);
                        request.setMissionType(dataset.mission_1);
                        request.setAircraftFamily(dataset.aircraft_family_1);
                        request.getAircraftTypes().clear();
                        request.getAircraftVersions().clear();
                        request.addAircraftType(dataset.aircraft_type_1);
                        request.addAircraftVersion(dataset.aircraft_version_1);
                        request.setAta(dataset.part_example.getAta());
                    });
            parentRequest2 = dataSetInitializer.createRequest("parentRequest2");

            childRequest11 = createChildRequest(parentRequest1);

            orphanChildRequest = createChildRequest(null);


            requestRepository.refresh(parentRequest1);
            requestRepository.refresh(parentRequest2);

            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            constraintValidator = factory.getValidator();

            childRequestCreationDto = new ChildRequestDetailDto();
            childRequestCreationDto.setMissionTypeId(dataset.mission_1.getId());
            childRequestCreationDto.setClientIds(List.of(dataset.client_1.getId()));
            childRequestCreationDto.setDrtToInspect(120);
            childRequestCreationDto.setEnvironmentId(dataset.environment_1.getId());
            childRequestCreationDto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
            childRequestCreationDto.setPartId(dataset.part_link_routing.getTechnicalId());
            childRequestCreationDto.setSerialNumbers(LIST_SERIAL_NUMBERS);
            childRequestCreationDto.setStatus(EnumStatus.VALIDATED);
            childRequestCreationDto.setAircraftFamilyId(dataset.aircraft_family_1.getId());
            childRequestCreationDto.setAircraftTypeIds(List.of());
            childRequestCreationDto.setAircraftVersionIds(List.of());
        });
    }


    @Test
    public void update_ko_childRequestIsOrphan() {
        checkFunctionalException(orphanChildRequest.getId(), null, "retex.error.childrequest.is.orphan");
    }

    @Test
    public void update_ko_childRequestBadId() {
        checkNotFoundException(1234L, null, "retex.error.childrequest.not.found");
    }

    @Test
    public void update_ko_serialNumberNotValid() {
        checkFunctionalException(childRequest11.getId(),
                createChildRequestDetailDto(childRequest11, crdto -> {
                    crdto.setSerialNumbers(List.of("100__01152"));
                    crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                }),
                "retex.error.childrequest.serialnumber.not.valid"
        );
    }

    @Test
    public void update_ko_badAircraftFamilyId() {

        checkFunctionalException(
                childRequest11.getId(),
                createChildRequestDetailDto(childRequest11,
                        crdto -> {
                            crdto.setAircraftFamilyId(1234L);
                        }
                ),
                "retex.error.aircraft.family.not.found");
    }

    @Test
    public void update_ok_parent_family_null_child_family_notNull() throws FunctionalException {
        parentRequest = dataSetInitializer.createRequest("parentRequest", request -> {
            request.setAircraftFamily(null);
            request.getAircraftTypes().clear();
            request.getAircraftVersions().clear();
        });
        childRequest = createChildRequest(parentRequest);
        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.setStatus(EnumStatus.CREATED);
                    crdto.setAircraftFamilyId(dataset.aircraft_family_2.getId());
                    crdto.setSerialNumbers(LIST_SERIAL_NUMBERS);
                    crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                })
        );
        checkAircraftUpdateResults(childRequest.getId(), result,
                dataset.aircraft_family_2.getId(), null, null);
    }

    @Test
    public void update_ok_parent_family_notNull_child_family_identical() throws FunctionalException {
        parentRequest = dataSetInitializer.createRequest("parentRequest", request -> {
            request.setAircraftFamily(dataset.aircraft_family_2);
            request.getAircraftTypes().clear();
            request.getAircraftVersions().clear();
        });
        childRequest = createChildRequest(parentRequest);
        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest.getId(),
                createChildRequestDetailDto(childRequest));
        checkAircraftUpdateResults(childRequest.getId(), result,
                dataset.aircraft_family_2.getId(), null, null);
    }

    @Test
    public void update_ko_parent_family_notNull_child_family_null() throws FunctionalException {
        parentRequest = dataSetInitializer.createRequest("parentRequest");
        childRequest = createChildRequest(parentRequest);
        checkFunctionalException(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.setAircraftFamilyId(null);
                    crdto.getAircraftTypeIds().clear();
                    crdto.getAircraftVersionIds().clear();
                    crdto.setSerialNumbers(LIST_SERIAL_NUMBERS);
                }),
                "retex.error.childrequest.aircraft.family.incompatible.with.parent.aircraft.family"
        );
    }

    // Parent AT = Not null , Child AT = Not Null
    @Test
    public void update_ok_parent_type_notNull_child_identical() throws FunctionalException {
        parentRequest = dataSetInitializer.createRequest("parentRequest", request -> {
            request.getAircraftTypes().clear();
            request.getAircraftVersions().clear();
        });
        childRequest = createChildRequest(parentRequest);
        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest.getId(),
                createChildRequestDetailDto(childRequest,
                        crdto -> {
                            crdto.setStatus(EnumStatus.CREATED);
                            crdto.getAircraftTypeIds().clear();
                            crdto.setSerialNumbers(LIST_SERIAL_NUMBERS);
                            crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                        }
                ));

        checkAircraftUpdateResults(childRequest.getId(), result,
                dataset.aircraft_family_1.getId(), null, null);

    }

    // Parent AF = Not null , Child AF = Null
    @Test
    public void update_ok_parent_type_null_child_type_notNull() throws FunctionalException {
        parentRequest = dataSetInitializer.createRequest("parentRequest", request -> {
            request.getAircraftTypes().clear();
            request.getAircraftVersions().clear();
        });
        childRequest = createChildRequest(parentRequest);
        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.setAircraftTypeIds(List.of(dataset.aircraft_type_1.getId()));
                    crdto.setSerialNumbers(LIST_SERIAL_NUMBERS);
                    crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                })
        );
        checkAircraftUpdateResults(childRequest.getId(), result,
                dataset.aircraft_family_1.getId(), dataset.aircraft_type_1.getId(), null);
    }

    @Test
    public void update_ok_parent_type_notNull_child_type_identical() throws FunctionalException {
        parentRequest = dataSetInitializer.createRequest("parentRequest", request -> {
            request.getAircraftVersions().clear();
        });
        childRequest = createChildRequest(parentRequest);
        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest.getId(),
                createChildRequestDetailDto(childRequest));
        checkAircraftUpdateResults(childRequest.getId(), result,
                dataset.aircraft_family_1.getId(), dataset.aircraft_type_1.getId(), null);
    }

    @Test
    public void update_ko_parent_type_notNull_child_type_null() throws FunctionalException {
        parentRequest = dataSetInitializer.createRequest("parentRequest");
        childRequest = createChildRequest(parentRequest);
        checkFunctionalException(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.getAircraftTypeIds().clear();
                    crdto.getAircraftVersionIds().clear();
                    crdto.setSerialNumbers(LIST_SERIAL_NUMBERS);
                    crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                }),
                "retex.error.childrequest.aircraft.type.incompatible.with.parent.aircraft.type"
        );
    }

    @Test
    public void update_ok_parent_version_null_child_version_notNull() throws FunctionalException {
        parentRequest = dataSetInitializer.createRequest("parentRequest", request -> {
            request.getAircraftVersions().clear();
        });
        childRequest = createChildRequest(parentRequest);
        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.setAircraftVersionIds(List.of(dataset.aircraft_version_1.getId()));
                    crdto.setSerialNumbers(LIST_SERIAL_NUMBERS);
                    crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                })
        );
        checkAircraftUpdateResults(childRequest.getId(), result,
                dataset.aircraft_family_1.getId(), dataset.aircraft_type_1.getId(), dataset.aircraft_version_1.getId());
    }

    @Test
    public void update_ok_parent_version_notNull_child_version_identical() throws FunctionalException {
        parentRequest = dataSetInitializer.createRequest("parentRequest");
        childRequest = createChildRequest(parentRequest);
        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest.getId(),
                createChildRequestDetailDto(childRequest)
        );
        checkAircraftUpdateResults(childRequest.getId(), result,
                dataset.aircraft_family_1.getId(), dataset.aircraft_type_1.getId(), dataset.aircraft_version_1.getId());
    }

    @Test
    public void update_ko_parent_version_notNull_child_version_null() throws FunctionalException {
        parentRequest = dataSetInitializer.createRequest("parentRequest");
        childRequest = createChildRequest(parentRequest);
        checkFunctionalException(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                     crdto.getAircraftVersionIds().clear();
                }),
                "retex.error.childrequest.aircraft.version.incompatible.with.parent.aircraft.version"
        );
    }

    // Parent Mission Type = null
    // Child update Mission Type != null
    @Test
    public void update_ok_parent_MissionTypeNULL_CHILD_MissionType_NOTNULL() throws FunctionalException {
        Request parentRequest = dataSetInitializer.createRequest("parentRequest", request -> {
            request.setMissionType(null);
        });
        ChildRequest childRequest = createChildRequest(parentRequest);
        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.setMissionTypeId(dataset.mission_1.getId());
                    crdto.setSerialNumbers(LIST_SERIAL_NUMBERS);
                    crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                })
        );
        assertThat(result.getMissionTypeId(), equalTo(dataset.mission_1.getId()));
    }

    // Parent Mission Type = not null
    // Child update Mission Type == null
    @Test
    public void update_ok_parent_MissionTypeNOTNull_CHILD_MissionType_NULL() throws FunctionalException {
        Request parentRequest = dataSetInitializer.createRequest("parentRequest");
        ChildRequest childRequest = createChildRequest(parentRequest);
        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.setMissionTypeId(null);
                    crdto.setSerialNumbers(LIST_SERIAL_NUMBERS);
                    crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                })
        );
        assertThat(result.getMissionTypeId(), nullValue());
    }

    // Parent Environment = null
    // Child update Environment != null
    @Test
    public void update_ok_parent_EnvironmentNULL_CHILD_Environment_NOTNULL() throws FunctionalException {
        Request parentRequest = dataSetInitializer.createRequest("parentRequest", request -> {
            request.setEnvironment(null);
        });
        ChildRequest childRequest = createChildRequest(parentRequest);
        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.setEnvironmentId(dataset.environment_1.getId());
                    crdto.setEnvironmentId(dataset.environment_1.getId());
                    crdto.setSerialNumbers(LIST_SERIAL_NUMBERS);
                })
        );
        assertThat(result.getEnvironmentId(), equalTo(dataset.environment_1.getId()));
    }

    // Parent Environment = not null
    // Child update Environment  == null
    @Test
    public void update_ok_parent_EnvironmentNOTNull_CHILD_Environment_NULL() throws FunctionalException {
        Request parentRequest = dataSetInitializer.createRequest("parentRequest");
        ChildRequest childRequest = createChildRequest(parentRequest);
        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.setEnvironmentId(null);
                    crdto.setSerialNumbers(LIST_SERIAL_NUMBERS);
                    crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                })
        );
        assertThat(result.getEnvironmentId(), nullValue());
    }

    // Parent Clients = null
    // Child update Clients  != null
    @Test
    public void update_ok_parent_Clients_NULL_CHILD_Client_NotNULL() throws FunctionalException {
        Request parentRequest = dataSetInitializer.createRequest("parentRequest", request -> {
            request.getClients().clear();
        });
        List<Long> clientIdsForUpdate = List.of(dataset.client_1.getId());
        ChildRequest childRequest = createChildRequest(parentRequest);
        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.setClientIds(clientIdsForUpdate);
                    crdto.setSerialNumbers(LIST_SERIAL_NUMBERS);
                    crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                })
        );
        assertTrue(result.getClientIds().containsAll(clientIdsForUpdate));
    }

    // Parent Clients != null
    // Child update Clients  != null (parent subset)
    @Test
    public void update_ok_parent_Clients_NONULL_CHILD_Client_NotNULL() throws FunctionalException {
        Request parentRequest = dataSetInitializer.createRequest("parentRequest", request -> {
            request.getClients().clear();
            request.addClient(dataset.client_1);
            request.addClient(dataset.client_2);
        });
        List<Long> clientIdsForUpdate = List.of(dataset.client_2.getId());
        ChildRequest childRequest = createChildRequest(parentRequest);
        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.setClientIds(clientIdsForUpdate);
                    crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                })
        );
        assertTrue(result.getClientIds().containsAll(clientIdsForUpdate));
        ChildRequest crUpdated = childRequestRepository.getOne(childRequest.getId());
        assertTrue(crUpdated.getClients().stream().map(c -> c.getId()).collect(Collectors.toList()).containsAll(clientIdsForUpdate));
    }

    // Parent Clients != null
    // Child update Clients  != null (not parent subset)
    @Test
    public void update_KO_parent_Clients_NOTNULL_CHILD_Client_NotNULL_not_subset() throws FunctionalException {
        Request parentRequest = dataSetInitializer.createRequest("parentRequest");
        List<Long> clientIdsForUpdate = List.of(dataset.client_2.getId());
        ChildRequest childRequest = createChildRequest(parentRequest);
        checkFunctionalException(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.setClientIds(clientIdsForUpdate);
                    crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                }),
                "retex.error.childrequest.clients.are.not.subset.of.parent.request"
        );
    }

    @Test
    public void update_ko_childRequestAircraftTypeIncompatibleWithAircraftFamily() {
        Request parentRequest = dataSetInitializer.createRequest("parentRequest", request -> {
            request.setAircraftFamily(null);
            request.getAircraftTypes().clear();
            request.getAircraftVersions().clear();
        });
        ChildRequest childRequest = createChildRequest(parentRequest);
        checkFunctionalException(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.setAircraftFamilyId(dataset.aircraft_family_1.getId());
                    crdto.setAircraftTypeIds(List.of(dataset.aircraft_type_3.getId()));
                }),
                "retex.error.aircraft.type.incompatible.with.aircraft.family"
        );
    }

    @Test
    public void update_ko_childRequestAircraftVersionIncompatibleWithAircraftType() {
        Request parentRequest = dataSetInitializer.createRequest("parentRequest", request -> {
            request.setAircraftFamily(null);
            request.getAircraftTypes().clear();
            request.getAircraftVersions().clear();
        });
        ChildRequest childRequest = createChildRequest(parentRequest);
        checkFunctionalException(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.setAircraftFamilyId(dataset.aircraft_family_1.getId());
                    crdto.setAircraftTypeIds(List.of(dataset.aircraft_type_1.getId()));
                    crdto.setAircraftVersionIds(List.of(dataset.aircraft_version_3.getId()));
                }),
                "retex.error.aircraft.version.incompatible.with.aircraft.type"
        );
    }

    @Test
    public void update_ok_serialNumbers() throws FunctionalException {
        List<String> sns = new ArrayList<String>() {{
            add("sn1");
            add("sn2");
        }};


        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest11.getId(),
                createChildRequestDetailDto(childRequest11,
                        crdto -> {
                            crdto.setSerialNumbers(sns);
                            crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                        }
                ));

        assertTrue(result.getSerialNumbers().containsAll(sns));
        ChildRequest crUpdated = childRequestRepository.getOne(childRequest11.getId());
        assertTrue(crUpdated.getPhysicalParts().stream().map(c -> c.getSerialNumber()).collect(Collectors.toList()).containsAll(sns));

    }

    @Test
    public void update_ok_media() throws FunctionalException {
        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest11.getId(),
                createChildRequestDetailDto(childRequest11,
                        crdto -> {
                            crdto.setStatus(EnumStatus.CREATED);
                            crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                            crdto.setSerialNumbers(LIST_SERIAL_NUMBERS);
                        }
                ));

        assertThat(result.getMedias().size(), equalTo(childRequest11.getMedias().size()));
        ChildRequest crUpdated = childRequestRepository.getOne(childRequest11.getId());
        assertThat(crUpdated.getMedias().size(), equalTo(result.getMedias().size()));

    }


    @Test
    public void update_ko_serialNumberEmpty() throws FunctionalException {
        checkFunctionalException(childRequest11.getId(),
                createChildRequestDetailDto(childRequest11,
                        crdto -> {
                            crdto.setSerialNumbers(List.of(""));
                            crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                        }),
                "retex.error.childrequest.serialnumber.not.found");

    }

    @Test
    public void update_ko_bad_media_uuid() {
        checkNotFoundException(childRequest11.getId(),
                createChildRequestDetailDto(childRequest11,
                        crdto -> {
                            crdto.setStatus(EnumStatus.CREATED);
                            crdto.setMedias(List.of(UUID.randomUUID()));
                        }),
                "retex.error.childrequest.mediaUUID.not.found");
    }

    @Test
    public void update_ok_part() throws FunctionalException {
        parentRequest = dataSetInitializer.createRequest("parentRequest", request -> {
            request.setAta(dataset.ata_1);
        });
        Part childPart = dataSetInitializer.createPart(part -> {
            part.setPartNumber("3334598");
            part.setStatus(EnumStatus.VALIDATED);
            part.setAta(dataset.ata_1);
        }, null);
        dataset.routing_1.setPart(childPart);
        routingRepository.save(dataset.routing_1);
        childRequest = createChildRequest(parentRequest, cr -> {
            cr.setRouting(null);
            cr.setRoutingNaturalId(null);
            cr.setStatus(EnumStatus.CREATED);
            cr.setRouting(dataset.routing_1);
        });
        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.setPartId(childPart.getNaturalId());
                    crdto.setSerialNumbers(LIST_SERIAL_NUMBERS);
                    crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                })
        );
        assertThat(result.getPartId(), equalTo(childPart.getNaturalId()));
        ChildRequest crUpdated = childRequestRepository.getOne(childRequest.getId());
        assertThat(crUpdated.getRouting().getPart().getNaturalId(), equalTo(childPart.getNaturalId()));
    }

    @Test
    public void update_ko_partIdInvalid() throws FunctionalException {

        checkNotFoundException(childRequest11.getId(),
                createChildRequestDetailDto(childRequest11,
                        crdto->{
                            crdto.setPartId(1234L);
                        }),
                "retex.error.childrequest.part.not.found");

    }

    @Test
    public void update_ko_partIncompatibleATACode() {

        parentRequest = dataSetInitializer.createRequest("parentRequest", request -> {
            request.setAta(dataset.ata_1);
        });
        childRequest = createChildRequest(parentRequest);
        Part childPart = dataSetInitializer.createPart(part -> {
            part.setPartNumber("3334598");
            part.setStatus(EnumStatus.VALIDATED);
            part.setAta(dataset.ata_2);
        }, null);
        checkFunctionalException(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.setPartId(childPart.getNaturalId());
                }),
                "retex.error.childrequest.part.atacode.incompatible.with.parent.ata.code"
        );
    }

    @Test
    public void update_ok_numberOfDrt() throws FunctionalException {
        Integer drtToInspect = 200;
        childRequest = createChildRequest(parentRequest1, cr -> {
            cr.setDrtToInspect(0L);
            cr.setRouting(dataset.routing_1);
            cr.setStatus(EnumStatus.CREATED);
        });
        ChildRequestDetailDto result = childRequestService.updateChildRequest(childRequest.getId(),
                createChildRequestDetailDto(childRequest, crdto -> {
                    crdto.setDrtToInspect(drtToInspect);
                    crdto.setSerialNumbers(LIST_SERIAL_NUMBERS);
                    crdto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
                })
        );
        assertThat(result.getDrtToInspect(), equalTo(drtToInspect));
        ChildRequest crUpdated = childRequestRepository.getOne(childRequest.getId());
        assertThat(crUpdated.getDrtToInspect().longValue(), equalTo(drtToInspect.longValue()));
    }


    @Test
    public void updateChildRequest_KO_SerialNumberInvalid() {
        childRequestCreationDto.setSerialNumbers(List.of(INVALID_SERIAL_NUMBER));

        checkViolatedConstraint(childRequestCreationDto, "Serial Number must contain only alphanumeric values");
    }

    @Test
    public void updateChildRequest_KO_DrtToInspectIsNegative() {
        childRequestCreationDto.setDrtToInspect(INVALID_DRT_TO_INSPECT);
        checkViolatedConstraint(childRequestCreationDto, "Drt to inspect must contain a positive value");
    }

    private void checkViolatedConstraint(final ChildRequestDetailDto dto, final String message) {
        Set<ConstraintViolation<ChildRequestDetailDto>> violations = constraintValidator.validate(dto);
        assertEquals(1, violations.size());
        Optional<ConstraintViolation<ChildRequestDetailDto>> optConstraint = violations.stream().findFirst();
        if (optConstraint.isPresent()) {
            ConstraintViolation<ChildRequestDetailDto> violConst = optConstraint.get();
            assertTrue(violConst.getMessageTemplate().contains(message));
        }
    }

    private void checkFunctionalException(final Long creqID, final ChildRequestDetailDto crdto, final String expectedMessage) {
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            childRequestService.updateChildRequest(creqID, crdto);
        });
        assertEquals(expectedMessage, thrown.getMessage());
    }

    private void checkNotFoundException(final Long creqID, final ChildRequestDetailDto crdto, final String expectedMessage) {
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            childRequestService.updateChildRequest(creqID, crdto);
        });
        assertTrue(thrown.getMessage().contains(expectedMessage));
    }

    private ChildRequestDetailDto createChildRequestDetailDto(final ChildRequest childRequest) {
        return createChildRequestDetailDto(childRequest, crdto->{});
    }

    private ChildRequestDetailDto createChildRequestDetailDto(final ChildRequest childRequest, final Consumer<ChildRequestDetailDto> modifychildRequestDto) {
        ChildRequestDetailDto crDTO = new ChildRequestDetailDto();

        crDTO.setStatus(EnumStatus.CREATED);

//        if (null != childRequest.getRouting() && null != childRequest.getRouting().getPart()) {
//            crDTO.setPartId(childRequest.getRouting().getPart().getNaturalId());
//        }

        Optional.ofNullable(childRequest.getMedias())
                .ifPresent(l -> crDTO.setMedias(l.stream().map(Media::getUuid).collect(Collectors.toList())));
        crDTO.setDrtToInspect(childRequest.getDrtToInspect().intValue());
        Optional.ofNullable(childRequest.getClients())
                .ifPresent(l -> crDTO.setClientIds(l.stream().map(AbstractBaseModel::getId).collect(Collectors.toList())));
        Optional.ofNullable(childRequest.getEnvironment()).ifPresent(e -> crDTO.setEnvironmentId(e.getId()));
        Optional.ofNullable(childRequest.getMissionType()).ifPresent(m -> crDTO.setMissionTypeId(m.getId()));
        Optional.ofNullable(childRequest.getAircraftFamily()).ifPresent(m -> crDTO.setAircraftFamilyId(m.getId()));
        Optional.ofNullable(childRequest.getAircraftTypes())
                .ifPresent(l -> crDTO.setAircraftTypeIds(l.stream().map(AbstractBaseModel::getId).collect(Collectors.toList())));
        Optional.ofNullable(childRequest.getAircraftVersions())
                .ifPresent(l -> crDTO.setAircraftVersionIds(l.stream().map(AbstractBaseModel::getId).collect(Collectors.toList())));
        Optional.ofNullable(childRequest.getPhysicalParts())
                .ifPresent(physicalParts -> crDTO.setSerialNumbers(physicalParts.stream().map(PhysicalPart::getSerialNumber).collect(Collectors.toList())));

        modifychildRequestDto.accept(crDTO);
        return crDTO;
    }

    private void checkAircraftUpdateResults(Long childRequestId, ChildRequestDetailDto result, final Long aircraftFamilyID, final Long aircraftTypeID, final Long aircraftVersionID) {
        assertThat(result.getAircraftFamilyId(), equalTo(aircraftFamilyID));
        if (aircraftTypeID == null) {
            assertTrue(result.getAircraftTypeIds().isEmpty());
        } else {
            assertThat(result.getAircraftTypeIds(), containsInAnyOrder(equalTo(aircraftTypeID)));
        }
        if (aircraftVersionID == null) {
            assertTrue(result.getAircraftVersionIds().isEmpty());
        } else {
            assertThat(result.getAircraftVersionIds(), containsInAnyOrder(equalTo(aircraftVersionID)));
        }

        ChildRequest crUpdated = childRequestRepository.getOne(childRequestId);
        assertThat(crUpdated.getAircraftFamily().getId(), equalTo(aircraftFamilyID));
        if (aircraftTypeID == null) {
            assertTrue(crUpdated.getAircraftTypes().isEmpty());
        } else {
            assertThat(crUpdated.getAircraftTypes().stream().map(e -> e.getId()).collect(Collectors.toList()), containsInAnyOrder(equalTo(aircraftTypeID)));
        }
        if (aircraftVersionID == null) {
            assertTrue(crUpdated.getAircraftVersions().isEmpty());
        } else {
            assertThat(crUpdated.getAircraftVersions().stream().map(e -> e.getId()).collect(Collectors.toList()), containsInAnyOrder(equalTo(aircraftVersionID)));
        }
    }

    /**
     * Creates a Child Request and set the desired Parent request.
     *
     * @param parentRequest the parent request
     * @return the created ChildRequest.
     */
    private ChildRequest createChildRequest(final Request parentRequest) {
        return createChildRequest(parentRequest, cr->{});
    }
    private ChildRequest createChildRequest(final Request parentRequest, final Consumer<ChildRequest> modifychildRequest) {
        childRequest = dataSetInitializer.createChildRequest(cr -> {
            cr.setParentRequest(parentRequest);
            cr.setRoutingNaturalId(dataset.routing_1.getNaturalId());
            cr.setRouting(dataset.routing_1);
            cr.setStatus(EnumStatus.CREATED);
            if (parentRequest != null) {
                AircraftFamily af = parentRequest.getAircraftFamily();
                cr.setAircraftFamily(af);
                cr.getAircraftTypes().clear();
                parentRequest.getAircraftTypes().forEach(e -> cr.addAircraftType(e));
                cr.getAircraftVersions().clear();
                parentRequest.getAircraftVersions().forEach(e -> cr.addAircraftVersion(e));
                cr.setMissionType(parentRequest.getMissionType());;
            }
        });
        modifychildRequest.accept(childRequest);
        return childRequest;
    }


}
