package com.airbus.retex.service.request;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.request.RequestUpdateDto;
import com.airbus.retex.business.dto.user.ReferenceDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.admin.aircraft.AircraftFamily;
import com.airbus.retex.model.admin.aircraft.AircraftType;
import com.airbus.retex.model.admin.aircraft.AircraftVersion;
import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.persistence.request.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

public class RequestUpdateServiceIT extends AbstractServiceIT {

    @Autowired
    private IRequestService requestService;
    @Autowired
    private RequestRepository requestRepository;

    private Request parentRequest;
    private RequestUpdateDto requestUpdateDto;

    private AircraftFamily multiAircraftTestFamily;
    private List<AircraftType> multiAircraftTestTypes;
    private List<AircraftVersion> multiAircraftTestVersions;

    @BeforeEach
    public void before() {
        parentRequest = dataSetInitializer.createRequest("parentRequest1",
                request -> {
                    request.setEnvironment(null);
                    request.setMissionType(null);
                });

        dataSetInitializer.createChildRequest(childRequest -> {
            childRequest.setParentRequest(parentRequest);
            childRequest.setEnvironment(dataset.environment_1);
            childRequest.setMissionType(dataset.mission_1);
        });

        requestUpdateDto = new RequestUpdateDto();
        requestUpdateDto.setOetp("54A752");
        requestUpdateDto.setOriginId(dataset.ORIGIN_CIVP.getId());
        requestUpdateDto.setReference("RQ-76428753FG");
        requestUpdateDto.setClientIds(List.of(dataset.client_1.getId()));
        requestUpdateDto.setTechnicalManagerIds(List.of(dataset.user_simpleUser.getId()));
        requestUpdateDto.setOperatorIds(List.of(dataset.user_simpleUser.getId()));
        requestUpdateDto.setAircraftFamily(new ReferenceDto(dataset.aircraft_family_1.getId()));
        requestUpdateDto.getAircraftTypes().add(dataset.aircraft_type_1.getId());
        requestUpdateDto.getAircraftVersions().add(dataset.aircraft_version_1.getId());
        requestUpdateDto.setEnvironment(new ReferenceDto(dataset.environment_1.getId()));
        requestUpdateDto.setMissionType(new ReferenceDto(dataset.mission_1.getId()));

        multiAircraftTestFamily = null;
        multiAircraftTestTypes = new ArrayList<>();
        multiAircraftTestVersions = new ArrayList<>();
    }


    @Test
    public void updateRequest_ko_AircraftFamilyInvalidateChildren() throws Exception {
        requestUpdateDto.setAircraftFamily(new ReferenceDto(dataset.aircraft_family_2.getId()));
        assertError("retex.error.request.aircraft.family.invalidate.children");
    }


    @Test
    public void updateRequest_ko_AircraftTypeInvalidateChildren() throws Exception {
        requestUpdateDto.getAircraftTypes().clear();
        requestUpdateDto.getAircraftTypes().add(dataset.aircraft_type_2.getId());
        assertError("retex.error.request.aircraft.type.invalidate.children");
    }


    @Test
    public void updateRequest_ko_AircraftVersionInvalidateChildren() throws Exception {
        requestUpdateDto.getAircraftVersions().clear();
        requestUpdateDto.getAircraftVersions().add(dataset.aircraft_version_2.getId());
        assertError("retex.error.request.aircraft.version.invalidate.children");
    }


    @Test
    public void updateRequest_ko_EnvironmentInvalidateChildren() throws Exception {
        requestUpdateDto.setEnvironment(new ReferenceDto(dataset.environment_2.getId()));
        assertError("retex.error.request.environment.invalidate.children");
    }


    @Test
    public void updateRequest_ko_MissionTypeInvalidateChildren() throws Exception {
        requestUpdateDto.setMissionType(new ReferenceDto(dataset.mission_2.getId()));
        assertError("retex.error.request.missionType.invalidate.children");
    }


    @Test
    public void updateRequest_ok_multi_aircraft_equals_child() throws Exception {
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
        prepareUpdateRequestDtoForAircraftTest();
        assertOkMultiAircraft();
    }


    @Test
    public void updateRequest_ok_multi_aircraft_parent_null() throws Exception {
        AircraftFamily childFamily = dataset.aircraft_set_family_1;
        List<AircraftType> childTypes = List.of(dataset.aircraft_set_family_1_type_1);
        List<AircraftVersion> childVersions = List.of(dataset.aircraft_set_family_1_type_1_version_1);

        prepareRequestForAircraftTest(childFamily, childTypes, childVersions);
        prepareUpdateRequestDtoForAircraftTest();
        assertOkMultiAircraft();
    }


    @Test
    public void updateRequest_ok_multi_aircraft_add_type() throws Exception {
        AircraftFamily childFamily = dataset.aircraft_set_family_1;
        List<AircraftType> childTypes = List.of(dataset.aircraft_set_family_1_type_1);
        List<AircraftVersion> childVersions = List.of(dataset.aircraft_set_family_1_type_1_version_1);

        multiAircraftTestFamily = childFamily;
        multiAircraftTestTypes = new ArrayList<>(childTypes);
        multiAircraftTestVersions = new ArrayList<>(childVersions);

        assertTrue(multiAircraftTestTypes.add(dataset.aircraft_set_family_1_type_2));

        prepareRequestForAircraftTest(childFamily, childTypes, childVersions);
        prepareUpdateRequestDtoForAircraftTest();
        assertOkMultiAircraft();
    }


    @Test
    public void updateRequest_ok_multi_aircraft_add_version() throws Exception {
        AircraftFamily childFamily = dataset.aircraft_set_family_1;
        List<AircraftType> childTypes = List.of(dataset.aircraft_set_family_1_type_1);
        List<AircraftVersion> childVersions = List.of(dataset.aircraft_set_family_1_type_1_version_1);

        multiAircraftTestFamily = childFamily;
        multiAircraftTestTypes = new ArrayList<>(childTypes);
        multiAircraftTestVersions = new ArrayList<>(childVersions);

        assertTrue(multiAircraftTestVersions.add(dataset.aircraft_set_family_1_type_1_version_2));

        prepareRequestForAircraftTest(childFamily, childTypes, childVersions);
        prepareUpdateRequestDtoForAircraftTest();
        assertOkMultiAircraft();
    }


    @Test
    public void updateRequest_ko_multi_aircraft_incompatible_type() throws Exception {
        AircraftFamily childFamily = dataset.aircraft_set_family_1;
        List<AircraftType> childTypes = List.of(
                dataset.aircraft_set_family_1_type_1,
                dataset.aircraft_set_family_1_type_2
        );
        List<AircraftVersion> childVersions = List.of(dataset.aircraft_set_family_1_type_1_version_1);

        multiAircraftTestFamily = childFamily;
        multiAircraftTestTypes = new ArrayList<>(childTypes);
        multiAircraftTestVersions = new ArrayList<>(childVersions);

        assertTrue(multiAircraftTestTypes.remove(dataset.aircraft_set_family_1_type_2));

        prepareRequestForAircraftTest(childFamily, childTypes, childVersions);
        prepareUpdateRequestDtoForAircraftTest();
        assertError("retex.error.request.aircraft.type.invalidate.children");
    }


    @Test
    public void updateRequest_ko_multi_aircraft_incompatible_version() throws Exception {
        AircraftFamily childFamily = dataset.aircraft_set_family_1;
        List<AircraftType> childTypes = List.of(dataset.aircraft_set_family_1_type_1);
        List<AircraftVersion> childVersions = List.of(
                dataset.aircraft_set_family_1_type_1_version_1,
                dataset.aircraft_set_family_1_type_1_version_2
        );

        multiAircraftTestFamily = childFamily;
        multiAircraftTestTypes = new ArrayList<>(childTypes);
        multiAircraftTestVersions = new ArrayList<>(childVersions);

        assertTrue(multiAircraftTestVersions.remove(dataset.aircraft_set_family_1_type_1_version_1));

        prepareRequestForAircraftTest(childFamily, childTypes, childVersions);
        prepareUpdateRequestDtoForAircraftTest();
        assertError("retex.error.request.aircraft.version.invalidate.children");
    }


    @Test
    public void updateRequest_ko_multi_aircraft_invalid_type() throws Exception {
        AircraftFamily childFamily = dataset.aircraft_set_family_1;
        List<AircraftType> childTypes = List.of(dataset.aircraft_set_family_1_type_1);
        List<AircraftVersion> childVersions = List.of(dataset.aircraft_set_family_1_type_1_version_1);

        multiAircraftTestFamily = childFamily;
        multiAircraftTestTypes = new ArrayList<>(childTypes);
        multiAircraftTestVersions = new ArrayList<>(childVersions);

        assertTrue(multiAircraftTestTypes.add(dataset.aircraft_set_family_2_type_1));

        prepareRequestForAircraftTest(childFamily, childTypes, childVersions);
        prepareUpdateRequestDtoForAircraftTest();
        assertError("retex.error.aircraft.type.incompatible.with.aircraft.family");
    }


    @Test
    public void updateRequest_ko_multi_aircraft_invalid_version() throws Exception {
        AircraftFamily childFamily = dataset.aircraft_set_family_1;
        List<AircraftType> childTypes = List.of(dataset.aircraft_set_family_1_type_1);
        List<AircraftVersion> childVersions = List.of(dataset.aircraft_set_family_1_type_1_version_1);

        multiAircraftTestFamily = childFamily;
        multiAircraftTestTypes = new ArrayList<>(childTypes);
        multiAircraftTestVersions = new ArrayList<>(childVersions);

        assertTrue(multiAircraftTestVersions.add(dataset.aircraft_set_family_1_type_2_version_1));

        prepareRequestForAircraftTest(childFamily, childTypes, childVersions);
        prepareUpdateRequestDtoForAircraftTest();
        assertError("retex.error.aircraft.version.incompatible.with.aircraft.type");
    }


    private void assertError(String expectedMessage) {
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            requestService.updateRequestDetail(parentRequest.getId(), requestUpdateDto, true);
        });
        assertEquals(expectedMessage, thrown.getMessage());
    }


    private void assertOkMultiAircraft() {
        assertDoesNotThrow(() -> {
            requestService.updateRequestDetail(parentRequest.getId(), requestUpdateDto, true);
        });
        parentRequest = requestRepository.findById(parentRequest.getId()).get();

        if (multiAircraftTestFamily == null) {
            assertThat(parentRequest.getAircraftFamily(), nullValue());
        } else {
            assertThat(parentRequest.getAircraftFamily().getId(), equalTo(multiAircraftTestFamily.getId()));
        }
        assertThat(parentRequest.getAircraftTypes().stream().map(AbstractBaseModel::getId).collect(Collectors.toList()),
                containsInAnyOrder(multiAircraftTestTypes.stream().map(AbstractBaseModel::getId).collect(Collectors.toList()).toArray()));
        assertThat(parentRequest.getAircraftVersions().stream().map(AbstractBaseModel::getId).collect(Collectors.toList()),
                containsInAnyOrder(multiAircraftTestVersions.stream().map(AbstractBaseModel::getId).collect(Collectors.toList()).toArray()));
    }


    private void prepareRequestForAircraftTest(final AircraftFamily aircraftFamily, final List<AircraftType> aircraftTypes, final List<AircraftVersion> aircraftVersions) {
        int current_id = dataSetInitializer.getNext();
        parentRequest = dataSetInitializer.createRequest("Request_" + current_id, r->{
            r.setAircraftFamily(aircraftFamily != null ? dataset.aircraft_set_family_1 : null);
            r.getAircraftTypes().clear();
            r.getAircraftTypes().add(dataset.aircraft_set_family_1_type_1);
            r.getAircraftVersions().clear();
            r.getAircraftVersions().add(dataset.aircraft_set_family_1_type_1_version_1);

        });
        dataSetInitializer.createChildRequest(childRequest -> {
            childRequest.setParentRequest(parentRequest);
            childRequest.setEnvironment(dataset.environment_1);
            childRequest.setMissionType(dataset.mission_1);
            childRequest.setAircraftFamily(aircraftFamily);
            childRequest.getAircraftTypes().clear();
            childRequest.getAircraftTypes().addAll(aircraftTypes);
            childRequest.getAircraftVersions().clear();
            childRequest.getAircraftVersions().addAll(aircraftVersions);
        });
    }


    private void prepareUpdateRequestDtoForAircraftTest() {
        requestUpdateDto.setAircraftFamily(multiAircraftTestFamily != null ? new ReferenceDto(multiAircraftTestFamily.getId()) : null);
        requestUpdateDto.setAircraftTypes(multiAircraftTestTypes.stream().map(AbstractBaseModel::getId).collect(Collectors.toList()));
        requestUpdateDto.setAircraftVersions(multiAircraftTestVersions.stream().map(AbstractBaseModel::getId).collect(Collectors.toList()));
    }
}
