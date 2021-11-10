package com.airbus.retex.request;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.origin.OriginDto;
import com.airbus.retex.business.dto.request.RequestCreationDto;
import com.airbus.retex.helper.DtoHelper;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.aircraft.AircraftFamily;
import com.airbus.retex.model.admin.aircraft.AircraftType;
import com.airbus.retex.model.admin.aircraft.AircraftVersion;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.origin.Origin;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.admin.RoleRepository;
import com.airbus.retex.persistence.request.RequestRepository;
import com.airbus.retex.service.request.IRequestService;
import com.airbus.retex.utils.ConstantUrl;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class RequestControllerUpdateRequestIT extends BaseControllerTest {

    @Autowired
    private IRequestService service;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RoleRepository roleRepository;

    private RequestCreationDto dto;

    private ObjectNode withBody;
    private ArrayNode operatorIds;
    private ArrayNode technicalIds;
    private ArrayNode clientIds;
    private ObjectNode aircraftFamily;
    private ArrayNode aircraftTypes;
    private ArrayNode aircraftVersions;
    private ObjectNode missionType;
    private ObjectNode environment;
    private ArrayNode originMedias;
    private ArrayNode specMedias;

    @BeforeEach
    public void setup() {
        dto = DtoHelper.generateValidCreationDto();
    }


    @BeforeEach
    private void before() {
        dataSetInitializer.createUserFeature(FeatureCode.REQUEST, dataset.user_simpleUser, EnumRightLevel.WRITE);
        withBody = objectMapper.createObjectNode();

        operatorIds = objectMapper.createArrayNode();
        technicalIds = objectMapper.createArrayNode();
        clientIds = objectMapper.createArrayNode();
        originMedias = objectMapper.createArrayNode();
        specMedias = objectMapper.createArrayNode();

        operatorIds.add(dataset.user_partReader.getId());
        technicalIds.add(dataset.user_partReader.getId());
        clientIds.add(1);
        clientIds.add(2);
        originMedias.add(dataset.media_1.getUuid().toString());
        specMedias.add(dataset.media_1.getUuid().toString());

        aircraftFamily = objectMapper.createObjectNode();
        aircraftTypes =objectMapper.createArrayNode();
        aircraftVersions = objectMapper.createArrayNode();
        missionType = objectMapper.createObjectNode();
        environment = objectMapper.createObjectNode();

        aircraftFamily.put("id",dataset.aircraft_family_1.getId());

        aircraftTypes.add(dataset.aircraft_type_1.getId());

        aircraftVersions.add(dataset.aircraft_version_1.getId());

        missionType.put("id", dataset.mission_1.getId());

        environment.put("id",dataset.environment_1.getId());

        withBody.put("originId", dataset.ORIGIN_RETEX.getId());
        withBody.put("reference", "KGDR-8675U5SRF");
        withBody.put("originComment", "");
        withBody.put("originUrl", "");
        withBody.set("originMedias", originMedias);
        withBody.set("technicalManagerIds", technicalIds);
        withBody.set("operatorIds", operatorIds);
        withBody.put("specComment", "");
        withBody.set("specMedias", specMedias);
        withBody.put("oetp", "54A752");
        withBody.set("aircraftFamily", aircraftFamily);
        withBody.set("aircraftTypes", aircraftTypes);
        withBody.set("aircraftVersions", aircraftVersions);
        withBody.put("ataCode", dataset.ata_2.getCode());
        withBody.set("missionType", missionType);
        withBody.set("environment", environment);
        withBody.set("clientIds", clientIds);


    }
    @Test
    public void updateRequest_ok() throws Exception {
        AtomicReference<User> operator = new AtomicReference<>();
        AtomicReference<User> techResponsible = new AtomicReference<>();

        runInTransaction(() -> {
            operator.set(dataSetInitializer.createUser(dataset.airbusEntity_france, user -> {
                user.setEmail("ope1@email.net");
                user.setFirstName("ope");
                user.addRole(roleRepository.getOne(dataset.role_operator.getId()));
            }));

            techResponsible.set(dataSetInitializer.createUser(dataset.airbusEntity_france, user -> {
                user.setEmail("tech1@email.net");
                user.setFirstName("tech1");
                user.addRole(roleRepository.getOne(dataset.role_technical_responsible.getId()));
            }));
        });

        withBody.put("version", "F1");
        operatorIds.add(dataset.user_partReader.getId());
        operatorIds.add(operator.get().getId());
        technicalIds.add(dataset.user_partReader.getId());
        technicalIds.add(techResponsible.get().getId());
        withBody.set("operatorIds", operatorIds);
        withBody.set("technicalManagerIds", technicalIds);
        withBody.put("originId", dataset.ORIGIN_CIVP.getId());
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_simpleUser;
        check(dataset.request_update)
                .andExpect(jsonPath("$.originMedias[*]", notNullValue()))
                .andExpect(jsonPath("$.specMedias[*]", notNullValue()))
                .andExpect(jsonPath("$.technicalManagerIds[*]", containsInAnyOrder(3,9)))
                .andExpect(jsonPath("$.operatorIds[*]", containsInAnyOrder(3,8)))
                .andExpect(jsonPath("$.clientIds[*]", containsInAnyOrder(1,2)))
                .andExpect(jsonPath("$.environmentId").value(dataset.environment_1.getId()))
                .andExpect(jsonPath("$.missionTypeId").value(dataset.mission_1.getId()))
                .andExpect(jsonPath("$.aircraftFamily.id").value(dataset.aircraft_family_1.getId()))
                .andExpect(jsonPath("$.aircraftTypes[*]",hasSize(1)))
                .andExpect(jsonPath("$.aircraftTypes[0].id").value(dataset.aircraft_version_1.getId()))
                .andExpect(jsonPath("$.aircraftVersions[*]",hasSize(1)))
                .andExpect(jsonPath("$.aircraftVersions[0].id").value(dataset.aircraft_version_1.getId()));
        OriginDto origin = service.findRequestById(dataset.request_update.getId(), null).getOrigin();
        assertThat(origin.getId(), equalTo(dataset.ORIGIN_CIVP.getId()));
    }

    @Test
    public void updateRequest_origin_ok() throws Exception {
        Origin newOrigin = dataSetInitializer.createOrigin("Test", "");

        asUser = dataset.user_simpleUser;
        withBody.put("originId", newOrigin.getId());
        expectedStatus = HttpStatus.OK;

        check(dataset.request_update, request -> {
            assertThat(request.getOrigin().getId(), equalTo(newOrigin.getId()));
        }).andExpect(jsonPath("$.originId", equalTo(newOrigin.getId().intValue())));
    }

    @Test
    public void updateRequest_originMedia_ok() throws Exception {
        Media newMedia1 = dataSetInitializer.createMedia();
        originMedias.add(newMedia1.getUuid().toString());

        asUser = dataset.user_simpleUser;
        withBody.set("originMedias", originMedias);
        expectedStatus = HttpStatus.OK;

        check(dataset.request_update, request -> {
            assertThat(request.getOriginMedias().size(), equalTo(originMedias.size()));
        });
    }

    @Test
    public void updateRequest_aircraftFamily_ok() throws Exception {
        AircraftFamily newAircraftFamily = dataSetInitializer.createAircraftFamily();

        asUser = dataset.user_simpleUser;
        withBody.putObject("aircraftFamily").put("id", newAircraftFamily.getId());
        withBody.putArray("aircraftTypes"); // Clear aircraftTypes to avoid incompatibility with aircraftFamily
        withBody.putArray("aircraftVersions"); // Clear aircraftVersions to avoid incompatibility with aircraftTypes
        expectedStatus = HttpStatus.OK;

        check(dataset.request_update, request -> {
            assertThat(request.getAircraftFamily().getId(), equalTo(newAircraftFamily.getId()));
        }).andExpect(jsonPath("$.aircraftFamily.id", equalTo(newAircraftFamily.getId().intValue())));
    }

    @Test
    public void updateRequest_aircraftType_ok() throws Exception {
        String typeName1 = "updateRequest_aircraftType_ok_newType1";
        String typeName2 = "updateRequest_aircraftType_ok_newType2";
        AircraftType newAircraftType1 = dataSetInitializer.createAircraftType(typeName1, t -> {
            t.setAircraftFamily(dataset.aircraft_family_1);
            t.setAircraftFamilyId(dataset.aircraft_family_1.getId());
        });
        AircraftType newAircraftType2 = dataSetInitializer.createAircraftType(typeName2, t -> {
            t.setAircraftFamily(dataset.aircraft_family_1);
            t.setAircraftFamilyId(dataset.aircraft_family_1.getId());
        });

        asUser = dataset.user_simpleUser;
        ArrayNode aircraftTypes = objectMapper.createArrayNode();
        aircraftTypes.add(newAircraftType1.getId());
        aircraftTypes.add(newAircraftType2.getId());
        withBody.set("aircraftTypes", aircraftTypes);
        withBody.putArray("aircraftVersions"); // Clear aircraftVersions to avoid incompatibility with aircraftTypes
        expectedStatus = HttpStatus.OK;

        check(dataset.request_update, request -> {
            assertThat(request.getAircraftTypes().stream().map(e->e.getId()).collect(Collectors.toList()), containsInAnyOrder(
                    newAircraftType2.getId(),
                    newAircraftType1.getId()
            ));
        }).andExpect(jsonPath("$.aircraftTypes[*].name",containsInAnyOrder(typeName2, typeName1)));
     }

    @Test
    public void updateRequest_aircraftVersion_ok() throws Exception {
        String versionName1 = "updateRequest_aircraftVersion_ok_newVersion1";
        String versionName2 = "updateRequest_aircraftVersion_ok_newVersion2";
        AircraftVersion newAircraftVersion1 = dataSetInitializer.createAircraftVersion(versionName1, t -> {
            t.setAircraftType(dataset.aircraft_type_1);
            t.setAircraftTypeId(dataset.aircraft_type_1.getId());
        });
        AircraftVersion newAircraftVersion2 = dataSetInitializer.createAircraftVersion(versionName2, t -> {
            t.setAircraftType(dataset.aircraft_type_1);
            t.setAircraftTypeId(dataset.aircraft_type_1.getId());
        });

        asUser = dataset.user_simpleUser;
        ArrayNode aircraftVersions = objectMapper.createArrayNode();
        aircraftVersions.add(newAircraftVersion1.getId());
        aircraftVersions.add(newAircraftVersion2.getId());
        withBody.set("aircraftVersions", aircraftVersions);
        expectedStatus = HttpStatus.OK;

        check(dataset.request_update, request -> {
            assertThat(request.getAircraftVersions().stream().map(e->e.getId()).collect(Collectors.toList()), containsInAnyOrder(
                    newAircraftVersion2.getId(),
                    newAircraftVersion1.getId()
            ));
        }).andExpect(jsonPath("$.aircraftVersions[*].name",containsInAnyOrder(versionName2, versionName1)));
    }


    @Test
    public void updateRequest_forbidden() throws Exception {
        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_simpleUser2;
        check(dataset.request_update);
    }

    @Test
    public void updateRequest_withoutOriginId() throws Exception {
        withBody.putNull("originId");
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;
        check(dataset.request_update);
    }

    @Test
    public void updateRequest_withoutReference() throws Exception {
        withBody.putNull("reference");
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;
        check(dataset.request_update);
    }

    @Test
    public void updateRequest_withoutOetp() throws Exception {
        withBody.putNull("oetp");
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;
        check(dataset.request_update);
    }



    @Test
    public void updateRequest_withoutAtaCode() throws Exception {
        withBody.putNull("ataCode");
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;
        check(dataset.request_update);
    }


    @Test
    public void updateRequest_withoutClientIds() throws Exception {
        withBody.set("clientIds", objectMapper.createArrayNode());
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;
        check(dataset.request_update);
    }


    private ResultActions check(Request request) throws Exception {
        return check(request, null);
    }

    private ResultActions check(Request request, Consumer<Request> updatedRequestAssertion) throws Exception {
        return check(request.getId(), updatedRequestAssertion);
    }

    private ResultActions check(Long requestId) throws Exception {
        return check(requestId, null);
    }

    private ResultActions check(Long requestId, Consumer<Request> updatedRequestAssertion) throws Exception {
        withRequest = put("/api/requests/" + requestId + "/validate").content(withBody.toString());
        ResultActions result = abstractCheck();

        if(updatedRequestAssertion != null) {
            runInTransaction(() -> {
                Request request = requestRepository.getOne(requestId);
                updatedRequestAssertion.accept(request);
            });
        }

        return result;
    }


    @Test
    public void updateRequestHeader_Ok() throws Exception {
        dto.setAirbusEntityId(dataset.airbusEntity_canada.getId());
        LocalDate dueDate = LocalDate.now().plusDays(8);
        dto.setDueDate(dueDate);
        asUser = dataset.user_simpleUser;
        this.dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.WRITE);

        withRequest = put(ConstantUrl.API_REQUEST, dataset.request_1.getId()).content(objectMapper.writeValueAsBytes(dto));

        expectedStatus = HttpStatus.OK;


        assertFalse(dataset.request_1.getAirbusEntity().equals(dataset.airbusEntity_canada));
        assertFalse(dataset.request_1.getDueDate().equals(dueDate.toString()));

        ResultActions result = abstractCheck();
        result.andExpect(jsonPath("$.id", equalTo(dataset.request_1.getId().intValue())));
        result.andExpect(jsonPath("$.airbusEntity.id", equalTo(dataset.airbusEntity_canada.getId().intValue())));
        result.andExpect(jsonPath("$.name", equalTo("Request XY")));
        result.andExpect(jsonPath("$.description", equalTo("Test description")));
        result.andExpect(jsonPath("$.dueDate", equalTo(dueDate.toString())));
    }
}
