package com.airbus.retex.childRequest;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.aircraft.AircraftFamily;
import com.airbus.retex.model.ata.ATA;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ChildRequestCreateControllerIT extends BaseControllerTest {

    private static final String API_POST_CHILD_REQUESTS = "/api/requests/%d/child-requests";
    private static final Long INVALID_ID = 1234L;
    private static final String SERIAL_NUMBER = "100373";
    private static final String SERIAL_NUMBER_EXISTING = "100372";
    private ObjectNode withBody;
    private Request parentRequest = null;
    private ChildRequest childRequestExisting = null;
    private PhysicalPart physicalPart = null;
    private ATA ataCode = null;
    private Part partInvalidAtaCode = null;
    private static final Integer DRT_NUMBER = 120;


    @BeforeEach
    public void before() {
        runInTransaction(() -> {
            parentRequest = dataSetInitializer.createRequest("parentRequest1",
                    request -> {
                        request.setEnvironment(dataset.environment_1);
                        request.setMissionType(dataset.mission_1);
                        request.setAircraftFamily(dataset.aircraft_family_1);
                        request.getAircraftTypes().clear();
                        request.getAircraftVersions().clear();
                        request.addAircraftType(dataset.aircraft_type_1);
                        request.addAircraftVersion(dataset.aircraft_version_1);
                        request.setAta(dataset.part_example.getAta());
                        request.setAta(dataset.part_example.getAta());
                    });

            dataSetInitializer.createUserFeature(FeatureCode.REQUEST, dataset.user_simpleUser, EnumRightLevel.WRITE);

            dataSetInitializer.createRouting(dataset.part_example);
            Routing routingExisting = dataSetInitializer.createRouting(dataset.part_example_2);
            childRequestExisting = dataSetInitializer.createChildRequest(childRequest -> {
                childRequest.setRouting(routingExisting);
                childRequest.setRoutingNaturalId(routingExisting.getNaturalId());
            });
            physicalPart = dataSetInitializer.createPhysicalPart(childRequestExisting, SERIAL_NUMBER_EXISTING);
            ataCode = dataSetInitializer.createATA();
            partInvalidAtaCode = dataSetInitializer.createPart(part -> {
                part.setAta(ataCode);
            }, null);
            dataSetInitializer.createRouting(partInvalidAtaCode);
            withBody = buildCreateChildRequestBody();
        });
    }

    @Test
    public void createChildRequest_OK() throws Exception {
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_simpleUser;
        checkResponseContentType = false;

        checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
    }


    @Test
    public void createChildRequest_KO_Forbidden() throws Exception {
        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_simpleUser2;

        checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
    }

    @Test
    public void createChildRequest_KO_PartNotFound() throws Exception {
        expectedStatus = HttpStatus.NOT_FOUND;
        asUser = dataset.user_simpleUser;

        withBody.put("partId", INVALID_ID);

        ResultActions result = checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
        result.andExpect(jsonPath("$.messages").value("The given part not found"));
    }

    @Test
    public void createChildRequest_KO_Request_NotFound() throws Exception {
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;

        ResultActions result = checkMethodPost(String.format(API_POST_CHILD_REQUESTS, INVALID_ID));
        result.andExpect(jsonPath("$.messages").value("Request not found"));
    }

    @Test
    public void createChildRequest_KO_ExistingSerialNumber() throws Exception {
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;

        withBody.putArray("serialNumbers").add(SERIAL_NUMBER_EXISTING);

        ResultActions result = checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
        result.andExpect(jsonPath("$.messages")
                .value("A child request already have following part number/serial number couple: "+dataset.part_example.getPartNumber()+ "/" + SERIAL_NUMBER_EXISTING));
    }
    @Test
    public void createChildRequest_KO_serialNumberMustBeAlphaNumeric() throws Exception {
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.WRITE);
        String serialNumber1 = "_ABCD";
        withBody.putArray("serialNumbers").add(serialNumber1);
        ResultActions result = checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
        result.andExpect(jsonPath("$.messages[0]").value("Serial Number must contain only alphanumeric values"));
    }
    @Test
    public void createChildRequest_KO_MissionTypeNotFound() throws Exception {
        expectedStatus = HttpStatus.NOT_FOUND;
        asUser = dataset.user_simpleUser;

        withBody.put("missionTypeId", INVALID_ID);

        ResultActions result = checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
        result.andExpect(jsonPath("$.messages").value("The given mission type not found"));
    }

    @Test
    public void createChildRequest_KO_MissionType_IdInvalidToParentRequest() throws Exception {
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;

        withBody.put("missionTypeId", dataset.mission_2.getId());

        ResultActions result = checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
        result.andExpect(jsonPath("$.messages")
                .value("The given mission type is incompatible with parent mission type"));
    }

    @Test
    public void createChildRequest_KO_Environment_NotFound() throws Exception {
        expectedStatus = HttpStatus.NOT_FOUND;
        asUser = dataset.user_simpleUser;

        withBody.put("environmentId", INVALID_ID);

        ResultActions result = checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
        result.andExpect(jsonPath("$.messages").value("The given environment not found"));
    }

    @Test
    public void createChildRequest_KO_Environment_IdInvalidToParentRequest() throws Exception {
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;

        withBody.put("environmentId", dataset.environment_2.getId());

        ResultActions result = checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
        result.andExpect(jsonPath("$.messages")
                .value("The given environment is incompatible with parent environment"));
    }

    @Test
    public void createChildRequest_KO_Part_ataCodeInvalidToParentRequest() throws Exception {
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;


        withBody.put("partId", partInvalidAtaCode.getNaturalId());

        ResultActions result = checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
        result.andExpect(jsonPath("$.messages")
                .value("The given part has an incompatible ATA code with the parent request"));
    }

    @Test
    public void createChildRequest_KO_Part_AtaEmpty() throws Exception {
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;
        partInvalidAtaCode = dataSetInitializer.createPart(part -> part.setAta(null), null);

        withBody.put("partId", partInvalidAtaCode.getNaturalId());

        ResultActions result = checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
        result.andExpect(jsonPath("$.messages")
                .value("The given part has an incompatible ATA code with the parent request"));
    }

    @Test
    public void createChildRequest_KO_AirCraftFamily_NotFound() throws Exception {
        expectedStatus = HttpStatus.NOT_FOUND;
        asUser = dataset.user_simpleUser;

        withBody.put("aircraftFamilyId", INVALID_ID);

        ResultActions result = checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
        result.andExpect(jsonPath("$.messages").value("The given aircraft family is not found"));
    }

    @Test
    public void createChildRequest_KO_AirCraftFamily_IdInvalidToParentRequest() throws Exception {
        AircraftFamily newAircraftFamily = dataSetInitializer.createAircraftFamily();

        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.BAD_REQUEST;

        withBody.put("aircraftFamilyId", newAircraftFamily.getId());
        withBody.putArray("aircraftTypeIds"); // Clear aircraftTypes to avoid incompatibility with aircraftFamily
        withBody.putArray("aircraftVersionIds"); // Clear aircraftVersions to avoid incompatibility with aircraftTypes

        ResultActions result = checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
        result.andExpect(jsonPath("$.messages")
                .value("The given aircraft family is incompatible with parent request aircraft family"));
    }

    @Test
    public void createChildRequest_KO_AirCraftVersion_NotFound() throws Exception {
        expectedStatus = HttpStatus.NOT_FOUND;
        asUser = dataset.user_simpleUser;

        withBody.putArray("aircraftVersionIds").add(INVALID_ID);

        ResultActions result = checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
        result.andExpect(jsonPath("$.messages").value("The given aircraft version is not found"));
    }

    @Test
    public void createChildRequest_KO_AirCraftVersion_InvalidWithaircraftType() throws Exception {
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;

        withBody.putArray("aircraftVersionIds").add(dataset.aircraft_version_2.getId());

        ResultActions result = checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
        result.andExpect(jsonPath("$.messages").value("The given aircraft version is incompatible with aircraft type"));
    }

    @Test
    public void createChildRequest_KO_AirCraftType_IdInvalidToParentRequest() throws Exception {
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;

        withBody.putArray("aircraftTypeIds").add(dataset.aircraft_type_2.getId());
        withBody.putArray("aircraftVersionIds"); // Clear aircraftVersions to avoid incompatibility with aircraftTypes

        ResultActions result = checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
        result.andExpect(jsonPath("$.messages")
                .value("The given aircraft type is incompatible with aircraft family"));
    }

    @Test
    public void createChildRequest_KO_Client_IdInvalidToParentRequest() throws Exception {
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;

        withBody.putArray("clientIds").add(dataset.client_2.getId());

        ResultActions result = checkMethodPost(String.format(API_POST_CHILD_REQUESTS, parentRequest.getId()));
        result.andExpect(jsonPath("$.messages")
                .value("The given list of clients are not sub set of list of the parent request"));
    }

    private ResultActions checkMethodPost(String endpoint) throws Exception {
        withRequest = post(endpoint).content(withBody.toString());
        return abstractCheck();
    }

    private ObjectNode buildCreateChildRequestBody() {
        ObjectNode nodes = objectMapper.createObjectNode();
        ArrayNode medias = objectMapper.createArrayNode();
        medias.add(dataset.mediaTmp_1.getUuid().toString());
        nodes.put("partId", dataset.part_example.getNaturalId());
        nodes.put("status", EnumStatus.VALIDATED.toString());
        nodes.put("aircraftFamilyId", dataset.aircraft_family_1.getId());
        nodes.putArray("aircraftVersionIds").add(dataset.aircraft_family_1.getId());
        nodes.putArray("aircraftTypeIds").add(dataset.aircraft_family_1.getId());
        nodes.put("environmentId", dataset.environment_1.getId());
        nodes.put("missionTypeId", dataset.mission_1.getId());
        nodes.put("drtToInspect", DRT_NUMBER);
        nodes.putArray("clientIds").add(dataset.client_1.getId());
        nodes.putArray("serialNumbers").add(SERIAL_NUMBER);
        nodes.set("medias", medias);
        return nodes;
    }
}

