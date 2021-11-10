package com.airbus.retex.service.childRequest;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.childRequest.ChildRequestDetailDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.admin.aircraft.AircraftFamily;
import com.airbus.retex.model.admin.aircraft.AircraftType;
import com.airbus.retex.model.admin.aircraft.AircraftVersion;
import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.client.Client;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChildRequestCreateServiceIT extends AbstractServiceIT {

    @Autowired
    private IChildRequestService childRequestService;

    @Autowired
    private ChildRequestRepository childRequestRepository;

    private ChildRequestDetailDto childRequestCreationDto;
    private Request request;
    private Routing routing;
    ChildRequest childRequest;
    Part part;
    private static final Integer DRT_NUMBER = 120;
    private static final String SERIAL_NUMBER = "100373";

    private AircraftFamily multiAircraftTestFamily;
    private List<AircraftType> multiAircraftTestTypes;
    private List<AircraftVersion> multiAircraftTestVersions;

    @BeforeEach
    public void before() {
        runInTransaction(() -> {
            part = dataSetInitializer.createPart(null);
            routing = dataSetInitializer.createRouting(r -> r.setStatus(EnumStatus.VALIDATED), part);
            request = dataSetInitializer.createRequest("request parent", request1 -> request1.getAta().setCode(part.getAta().getCode()));


            childRequestCreationDto = new ChildRequestDetailDto();
            childRequestCreationDto.setAircraftFamilyId(dataset.aircraft_family_1.getId());
            childRequestCreationDto.setMissionTypeId(request.getMissionType().getId());
            childRequestCreationDto.setAircraftTypeIds(List.of(dataset.aircraft_type_1.getId()));
            childRequestCreationDto.setAircraftVersionIds(List.of(dataset.aircraft_version_1.getId()));
            childRequestCreationDto.setClientIds(List.of(dataset.client_1.getId()));
            childRequestCreationDto.setDrtToInspect(DRT_NUMBER);
            childRequestCreationDto.setEnvironmentId(dataset.environment_1.getId());
            childRequestCreationDto.setMedias(List.of(dataset.mediaTmp_1.getUuid()));
            childRequestCreationDto.setPartId(part.getNaturalId());
            childRequestCreationDto.setSerialNumbers(List.of(SERIAL_NUMBER));
            childRequestCreationDto.setStatus(EnumStatus.VALIDATED);

            multiAircraftTestFamily = null;
            multiAircraftTestTypes = List.of();
            multiAircraftTestVersions = List.of();
        });
    }

    @Test
    public void createChildRequest_ok() throws FunctionalException {
        childRequestService.createChildRequest(request.getId(), childRequestCreationDto);
        childRequest = childRequestRepository.findByRoutingNaturalId(routing.getNaturalId()).get();
        assertThat(childRequest.getId(), notNullValue());
        assertThat(childRequest.getAircraftFamily().getId(), equalTo(dataset.aircraft_family_1.getId()));
        assertThat(childRequest.getAircraftVersions().stream().map(AircraftVersion::getId).collect(Collectors.toList()),
                containsInAnyOrder(equalTo(dataset.aircraft_version_1.getId())));
        assertThat(childRequest.getAircraftTypes().stream().map(AircraftType::getId).collect(Collectors.toList()),
                containsInAnyOrder(equalTo(dataset.aircraft_type_1.getId())));
        assertThat(childRequest.getClients().stream().map(Client::getId).collect(Collectors.toList()),
                containsInAnyOrder(List.of(dataset.client_1.getId()).toArray()));
        assertThat(childRequest.getDrtToInspect().intValue(), equalTo(DRT_NUMBER));
        assertThat(childRequest.getEnvironment().getId(), equalTo(dataset.environment_1.getId()));
        assertThat(childRequest.getRouting().getPart().getNaturalId(), equalTo(part.getNaturalId()));
        assertThat(childRequest.getParentRequest().getId(), equalTo(request.getId()));
        assertThat(childRequest.getPhysicalParts().stream().map(PhysicalPart::getSerialNumber).collect(Collectors.toList()),
                containsInAnyOrder(List.of(SERIAL_NUMBER).toArray()));
        assertThat(childRequest.getMedias().stream().map(Media::getUuid).collect(Collectors.toList()),
                containsInAnyOrder(List.of(dataset.mediaTmp_1.getUuid()).toArray()));
        assertThat(childRequest.getStatus(), equalTo(EnumStatus.VALIDATED));
    }

    @Test
    public void createChildRequest_ok_StatusCreated() throws FunctionalException {
        childRequestCreationDto.setStatus(EnumStatus.CREATED);
        childRequestService.createChildRequest(request.getId(), childRequestCreationDto);
        childRequest = childRequestRepository.findByRoutingNaturalId(routing.getNaturalId()).get();
        assertThat(childRequest.getStatus(), equalTo(EnumStatus.CREATED));
    }

    @Test
    public void createChildRequest_ko_StatusNotAllowed() {
        childRequestCreationDto.setStatus(EnumStatus.IN_PROGRESS);
        assertError("retex.error.child.request.status.not.allowed");
    }

    @Test
    public void createChildRequest_ok_ParentCheck() throws FunctionalException {
        childRequestService.createChildRequest(request.getId(), childRequestCreationDto);
        childRequest = childRequestRepository.findByRoutingNaturalId(routing.getNaturalId()).get();
        List<ChildRequest> childRequests = childRequestRepository.findByParentRequest(request);
        assertThat(childRequests.stream().map(ChildRequest::getId).collect(Collectors.toList()),
                containsInAnyOrder(List.of(childRequest.getId()).toArray()));

    }

    @Test
    public void createChildRequest_ok_multi_aircraft() {
        multiAircraftTestFamily = dataset.aircraft_set_family_1;
        multiAircraftTestTypes = List.of(
                dataset.aircraft_set_family_1_type_1,
                dataset.aircraft_set_family_1_type_2
        );
        multiAircraftTestVersions  = List.of(
                dataset.aircraft_set_family_1_type_1_version_1,
                dataset.aircraft_set_family_1_type_1_version_2,
                dataset.aircraft_set_family_1_type_2_version_1
        );
        prepareRequestForAircraftTest(null, List.of(), List.of());
        prepareChildRequestDtoForAircraftTest();
        assertOkMultiAircraft();
    }

    @Test
    public void createChildRequest_ok_multi_aircraft_equals_parent() {
        multiAircraftTestFamily = dataset.aircraft_set_family_1;
        multiAircraftTestTypes = List.of(
                dataset.aircraft_set_family_1_type_1,
                dataset.aircraft_set_family_1_type_2
        );
        multiAircraftTestVersions  = List.of(
                dataset.aircraft_set_family_1_type_1_version_1,
                dataset.aircraft_set_family_1_type_1_version_2,
                dataset.aircraft_set_family_1_type_2_version_1
        );
        prepareRequestForAircraftTest(multiAircraftTestFamily, multiAircraftTestTypes, multiAircraftTestVersions);
        prepareChildRequestDtoForAircraftTest();
        assertOkMultiAircraft();
    }

    @Test
    public void createChildRequest_ko_multi_aircraft_child_family_empty() {
        AircraftFamily parentAircraftFamily = dataset.aircraft_set_family_1;;

        prepareRequestForAircraftTest(parentAircraftFamily, List.of(), List.of());
        prepareChildRequestDtoForAircraftTest();
        assertError("retex.error.childrequest.aircraft.family.incompatible.with.parent.aircraft.family");
    }

    @Test
    public void createChildRequest_ko_multi_aircraft_child_type_empty() {
        AircraftFamily parentAircraftFamily = dataset.aircraft_set_family_1;;
        List<AircraftType> parentAircraftTypes = List.of(dataset.aircraft_set_family_1_type_1);
        multiAircraftTestFamily = parentAircraftFamily;

        prepareRequestForAircraftTest(parentAircraftFamily, parentAircraftTypes, List.of());
        prepareChildRequestDtoForAircraftTest();
        assertError("retex.error.childrequest.aircraft.type.incompatible.with.parent.aircraft.type");
    }

    @Test
    public void createChildRequest_ko_multi_aircraft_child_version_empty() {
        AircraftFamily parentAircraftFamily = dataset.aircraft_set_family_1;;
        List<AircraftType> parentAircraftTypes = List.of(dataset.aircraft_set_family_1_type_1);
        List<AircraftVersion> parentAircraftVersions = List.of(dataset.aircraft_set_family_1_type_1_version_1);
        multiAircraftTestFamily = parentAircraftFamily;
        multiAircraftTestTypes = List.copyOf(parentAircraftTypes);

        prepareRequestForAircraftTest(parentAircraftFamily, parentAircraftTypes, parentAircraftVersions);
        prepareChildRequestDtoForAircraftTest();
        assertError("retex.error.childrequest.aircraft.version.incompatible.with.parent.aircraft.version");
    }

    @Test
    public void createChildRequest_ko_multi_aircraft_incompatible_type() {
        multiAircraftTestFamily = dataset.aircraft_set_family_1;
        multiAircraftTestTypes = List.of(
                dataset.aircraft_set_family_1_type_1,
                dataset.aircraft_set_family_1_type_2
        );

        List<AircraftType> parentAircraftTypes = new ArrayList<>(multiAircraftTestTypes);
        assertTrue(parentAircraftTypes.remove(dataset.aircraft_set_family_1_type_2));

        prepareRequestForAircraftTest(dataset.aircraft_set_family_1, parentAircraftTypes, List.of());
        prepareChildRequestDtoForAircraftTest();
        assertError("retex.error.childrequest.aircraft.type.incompatible.with.parent.aircraft.type");
    }

    @Test
    public void createChildRequest_ko_multi_aircraft_incompatible_version() {
        multiAircraftTestFamily = dataset.aircraft_set_family_1;
        multiAircraftTestTypes = List.of(
                dataset.aircraft_set_family_1_type_1,
                dataset.aircraft_set_family_1_type_2
        );
        multiAircraftTestVersions  = List.of(
                dataset.aircraft_set_family_1_type_1_version_1,
                dataset.aircraft_set_family_1_type_1_version_2,
                dataset.aircraft_set_family_1_type_2_version_1
        );

        List<AircraftVersion> parentAircraftVersions = new ArrayList<>(multiAircraftTestVersions);
        assertTrue(parentAircraftVersions.remove(dataset.aircraft_set_family_1_type_1_version_2));

        prepareRequestForAircraftTest(dataset.aircraft_set_family_1, multiAircraftTestTypes, parentAircraftVersions);
        prepareChildRequestDtoForAircraftTest();
        assertError("retex.error.childrequest.aircraft.version.incompatible.with.parent.aircraft.version");
    }

    @Test
    public void createChildRequest_ko_multi_aircraft_invalid_type() {
        AircraftType invalid = dataset.aircraft_set_family_2_type_1;
        multiAircraftTestFamily = dataset.aircraft_set_family_1;
        multiAircraftTestTypes = new ArrayList<>(List.of(
                dataset.aircraft_set_family_1_type_1,
                invalid
        ));

        prepareRequestForAircraftTest(null, List.of(), List.of());
        prepareChildRequestDtoForAircraftTest();
        assertError("retex.error.aircraft.type.incompatible.with.aircraft.family");

        multiAircraftTestTypes.remove(invalid);
        prepareChildRequestDtoForAircraftTest();
        assertOkMultiAircraft();
    }

    @Test
    public void createChildRequest_ko_multi_aircraft_invalid_version() {
        AircraftVersion invalid = dataset.aircraft_set_family_2_type_1_version_1;
        multiAircraftTestFamily = dataset.aircraft_set_family_1;
        multiAircraftTestTypes = List.of(
                dataset.aircraft_set_family_1_type_1,
                dataset.aircraft_set_family_1_type_2
        );
        multiAircraftTestVersions  = new ArrayList<>(List.of(
                dataset.aircraft_set_family_1_type_1_version_1,
                dataset.aircraft_set_family_1_type_2_version_1,
                invalid
        ));

        prepareRequestForAircraftTest(null, List.of(), List.of());
        prepareChildRequestDtoForAircraftTest();
        assertError("retex.error.aircraft.version.incompatible.with.aircraft.type");

        multiAircraftTestVersions.remove(invalid);
        prepareChildRequestDtoForAircraftTest();
        assertOkMultiAircraft();
    }

    private void assertError(String expectedMessage) {
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            childRequestService.createChildRequest(request.getId(), childRequestCreationDto);
        });
        assertEquals(expectedMessage, thrown.getMessage());
    }

    private void assertOkMultiAircraft() {
        assertDoesNotThrow(() -> {
            childRequestService.createChildRequest(request.getId(), childRequestCreationDto);
        });
        childRequest = childRequestRepository.findByRoutingNaturalId(routing.getNaturalId()).get();

        if (multiAircraftTestFamily == null) {
            assertThat(childRequest.getAircraftFamily(), nullValue());
        } else {
            assertThat(childRequest.getAircraftFamily().getId(), equalTo(multiAircraftTestFamily.getId()));
        }
        assertThat(childRequest.getAircraftTypes().stream().map(AbstractBaseModel::getId).collect(Collectors.toList()),
                containsInAnyOrder(multiAircraftTestTypes.stream().map(AbstractBaseModel::getId).collect(Collectors.toList()).toArray()));
        assertThat(childRequest.getAircraftVersions().stream().map(AbstractBaseModel::getId).collect(Collectors.toList()),
                containsInAnyOrder(multiAircraftTestVersions.stream().map(AbstractBaseModel::getId).collect(Collectors.toList()).toArray()));
    }

    private void prepareRequestForAircraftTest(final AircraftFamily aircraftFamily, final List<AircraftType> aircraftTypes, final List<AircraftVersion> aircraftVersions) {
        int current_id = dataSetInitializer.getNext();
        request = dataSetInitializer.createRequest("Request_" + current_id, r->{
            r.getAta().setCode(part.getAta().getCode());
            r.setAircraftFamily(aircraftFamily);
            r.getAircraftTypes().clear();
            r.getAircraftTypes().addAll(aircraftTypes);
            r.getAircraftVersions().clear();
            r.getAircraftVersions().addAll(aircraftVersions);
        });
    }

    private void prepareChildRequestDtoForAircraftTest() {
        childRequestCreationDto.setAircraftFamilyId(multiAircraftTestFamily != null ? multiAircraftTestFamily.getId() : null);
        childRequestCreationDto.setAircraftTypeIds(multiAircraftTestTypes.stream().map(AbstractBaseModel::getId).collect(Collectors.toList()));
        childRequestCreationDto.setAircraftVersionIds(multiAircraftTestVersions.stream().map(AbstractBaseModel::getId).collect(Collectors.toList()));
    }
}
