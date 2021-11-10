package com.airbus.retex.childRequest;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.persistence.aircraft.AircraftFamilyRepository;
import com.airbus.retex.persistence.aircraft.AircraftTypeRepository;
import com.airbus.retex.persistence.aircraft.AircraftVersionRepository;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import com.airbus.retex.persistence.request.RequestRepository;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ChildRequestUpdateCloseDeleteControllerIT extends BaseControllerTest {
    @Autowired
    private ChildRequestRepository childRequestRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private AircraftFamilyRepository aircraftFamilyRepository;
    @Autowired
    private AircraftTypeRepository aircraftTypeRepository;
    @Autowired
    private AircraftVersionRepository aircraftVersionRepository;
    private static final String API_CHILD_REQUEST_ID = "/api/requests/%d/child-requests/%d";
    private static final String API_CHILD_REQUEST_CLOSE = API_CHILD_REQUEST_ID + "?status=%s";
    private static final String API_CHILD_REQUEST_DELETE = API_CHILD_REQUEST_ID ;
    private static final String API_CHILD_REQUEST_UPDATE = API_CHILD_REQUEST_ID;
    private static final Long INVALID_CHILD_REQUEST_ID = 1234L;
    private ObjectNode withBody;
    private ChildRequest testChildRequest = null;
    private ChildRequest testChildRequest2 = null;
    private PhysicalPart sn1 = null;
    private Request parentRequest1 = null;
    private Request parentRequest2 = null;
    private Validator constraintValidator;

    @BeforeEach
    public void before() {

        withBody = objectMapper.createObjectNode();
        withBody.putArray("aircraftTypeIds").add(dataset.aircraft_type_1.getId());
        withBody.putArray("aircraftVersionIds").add(dataset.aircraft_version_1.getId());
        withBody.put("aircraftFamilyId", dataset.aircraft_family_1.getId());

        parentRequest1 = dataSetInitializer.createRequest("parentRequest1",
                request->{
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
        testChildRequest = createChildRequest(parentRequest1);
        testChildRequest.addPhysicalPart(dataSetInitializer.createPhysicalPart(testChildRequest, "sn10"));
        testChildRequest.addPhysicalPart(dataSetInitializer.createPhysicalPart(testChildRequest, "sn20"));
        testChildRequest = childRequestRepository.save(testChildRequest);

        testChildRequest2 = createChildRequest(parentRequest1);
        //parentRequest2 = requestRepository.save(parentRequest2);
        //em.detach(parentRequest2);
        requestRepository.refresh(parentRequest2);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        constraintValidator = factory.getValidator();

    }

    /**
     * Creates a Child Request and set the desired Parent request.
     * @param parentRequest the parent request
     * @return the created ChildRequest.
     */
    private ChildRequest createChildRequest(final Request parentRequest) {
        if (parentRequest != null) {
            return dataSetInitializer.createChildRequest(cr -> {
                cr.setParentRequest(parentRequest);
                cr.setAircraftFamily(dataset.aircraft_family_1);
                cr.getAircraftTypes().add(dataset.aircraft_type_1);
                cr.getAircraftVersions().add(dataset.aircraft_version_1);
                cr.setStatus(EnumStatus.CREATED);
            });
        }else {
            return dataSetInitializer.createChildRequest(cr -> {
                cr.setParentRequest(null);

                cr.setStatus(EnumStatus.CREATED);
            });
        }
    }

    @Test
    public void closeChildRequest_KO_NotFound() throws Exception {
        expectedStatus = HttpStatus.NOT_FOUND;
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.WRITE);

        ResultActions result = checkPut(String.format(API_CHILD_REQUEST_CLOSE, parentRequest1.getId(), INVALID_CHILD_REQUEST_ID, EnumStatus.CLOSED));
        result.andExpect(jsonPath("$.messages[0]").value("Child request not found"));
    }


    @Test
    public void deleteChildRequest_KO_NotFound() throws Exception {
        expectedStatus = HttpStatus.NOT_FOUND;
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.WRITE);

        ResultActions result = checkDelete(String.format(API_CHILD_REQUEST_DELETE, parentRequest1.getId(), INVALID_CHILD_REQUEST_ID));
        result.andExpect(jsonPath("$.messages[0]").value("Child request not found"));
    }

    @Test
    public void updateChildRequest_KO_NotFound() throws Exception {
        expectedStatus = HttpStatus.NOT_FOUND;
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.WRITE);

        ResultActions result = checkPut(String.format(API_CHILD_REQUEST_UPDATE, parentRequest1.getId(), INVALID_CHILD_REQUEST_ID));
        result.andExpect(jsonPath("$.messages[0]").value("Child request not found"));
    }


    @Test
    public void closeChildRequest_KO_StatusIsForbidden()  throws Exception{
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.FORBIDDEN;
        checkPut(String.format(API_CHILD_REQUEST_CLOSE, parentRequest1.getId(), testChildRequest.getId(), EnumStatus.CLOSED));

        //abstractCheck();

    }


    @Test
    public void deleteChildRequest_KO_StatusIsForbidden()  throws Exception{
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.FORBIDDEN;
        withRequest = delete(String.format(API_CHILD_REQUEST_DELETE, parentRequest1.getId(), testChildRequest.getId()));

        abstractCheck();

    }


    @Test
    public void updateChildRequest_KO_StatusIsForbidden()  throws Exception{
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.FORBIDDEN;
        checkPut(String.format(API_CHILD_REQUEST_UPDATE, parentRequest1.getId(), testChildRequest.getId()));

        //abstractCheck();

    }

    @Test
    public void closeChildRequest_OK()  throws Exception{
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.NO_CONTENT;
        dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.WRITE);
        checkPut(String.format(API_CHILD_REQUEST_CLOSE, parentRequest1.getId(), testChildRequest.getId(), EnumStatus.CLOSED));

        //abstractCheck();

    }

    @Test
    public void deleteChildRequest_OK()  throws Exception{
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.NO_CONTENT;
        dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.WRITE);
        withRequest = delete(String.format(API_CHILD_REQUEST_DELETE, parentRequest1.getId(), testChildRequest2.getId()));

        abstractCheck();

    }

    @Test
    public void updateChildRequest_KO_serialNumberMustBeAlphaNumeric() throws Exception {
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.WRITE);
        String serialNumber1 = "_ABCD";
        withBody.putArray("serialNumbers").add(serialNumber1);
        ResultActions result = checkPut(String.format(API_CHILD_REQUEST_UPDATE, parentRequest1.getId(), testChildRequest.getId()));
        result.andExpect(jsonPath("$.messages[0]").value("Serial Number must contain only alphanumeric values"));
    }

    @Test
    public void updateChildRequest_OK_multiUpdatesOfSerialNumbers() throws Exception {
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.WRITE);

        withBody.putArray("serialNumbers").add("A");
        withBody.put("status", EnumStatus.CREATED.name());
        ResultActions result = checkPut(String.format(API_CHILD_REQUEST_UPDATE, parentRequest1.getId(), testChildRequest.getId()));
        result.andExpect(jsonPath("$.serialNumbers[*]",containsInAnyOrder("A")));

        withBody = objectMapper.createObjectNode();
        withBody.putArray("serialNumbers").add("B");
        withBody.put("status", EnumStatus.CREATED.name());
        withBody.putArray("aircraftTypeIds").add(dataset.aircraft_type_1.getId());
        withBody.putArray("aircraftVersionIds").add(dataset.aircraft_version_1.getId());
        withBody.put("aircraftFamilyId", dataset.aircraft_family_1.getId());
        result = checkPut(String.format(API_CHILD_REQUEST_UPDATE, parentRequest1.getId(), testChildRequest.getId()));
        result.andExpect(jsonPath("$.serialNumbers[*]",containsInAnyOrder("B")));
        runInTransaction(()-> {
            ChildRequest foundChildRequest = childRequestRepository.getOne(testChildRequest.getId());
            List<String> serialNumbersOfFoundChildRequest = foundChildRequest.getPhysicalParts().stream().map(pp->pp.getSerialNumber())
                    .collect(Collectors.toList());
            ;
            assertTrue(serialNumbersOfFoundChildRequest.size()==1, "We should have only one serial number");
            assertTrue(serialNumbersOfFoundChildRequest.get(0).equals("B"), "We should have only one serial number");
        });

        withBody = objectMapper.createObjectNode();
        withBody.putArray("serialNumbers").add("SN1").add("SN2").add("SN3");
        withBody.putArray("aircraftTypeIds").add(dataset.aircraft_type_1.getId());
        withBody.putArray("aircraftVersionIds").add(dataset.aircraft_version_1.getId());
        withBody.put("aircraftFamilyId", dataset.aircraft_family_1.getId());
        withBody.put("status", EnumStatus.CREATED.name());
        result = checkPut(String.format(API_CHILD_REQUEST_UPDATE, parentRequest1.getId(), testChildRequest.getId()));
        result.andExpect(jsonPath("$.serialNumbers[*]",containsInAnyOrder("SN1","SN2","SN3")));
        runInTransaction(()-> {
            ChildRequest foundChildRequest = childRequestRepository.getOne(testChildRequest.getId());
            List<String> serialNumbersOfFoundChildRequest = foundChildRequest.getPhysicalParts().stream().map(pp->pp.getSerialNumber())
                    .collect(Collectors.toList());

            assertTrue(serialNumbersOfFoundChildRequest.size()==3, "We should have 3 serial numbers");
            assertTrue(serialNumbersOfFoundChildRequest.containsAll(Arrays.asList("SN1", "SN2", "SN3")), "We should have 3 serial numbers");
        });
    }

    @Test
    public void updateChildRequest_OK()  throws Exception{
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.OK;
        dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.WRITE);
        String serialNumber1 = "serialNumber1";
        String serialNumber2 = "serialNumber2";
        ArrayNode clientIds = objectMapper.createArrayNode();
        ArrayNode medias = objectMapper.createArrayNode();
        medias.add(dataset.mediaTmp_1.getUuid().toString());
        clientIds.add(dataset.client_1.getId());
        //clientIds.addOperation(dataset.client_2.getId());
        withBody.putArray("clientIds").add(dataset.client_1.getId());
        withBody.putArray("serialNumbers").add(serialNumber1).add(serialNumber2);
        withBody.set("medias",medias);
        Part part = dataSetInitializer.createPart(null);
        dataSetInitializer.createRouting(part);
        int number_of_drt_to_inspect = 105;

        withBody.put("aircraftFamilyId", dataset.aircraft_family_1.getId());
        withBody.put("missionTypeId", dataset.mission_1.getId());
        withBody.put("environmentId", dataset.environment_1.getId());
        withBody.put("partId", part.getNaturalId());
        withBody.put("drtToInspect", number_of_drt_to_inspect);
        withBody.put("status", EnumStatus.CREATED.name());

        ResultActions result = checkPut(String.format(API_CHILD_REQUEST_UPDATE, parentRequest1.getId(), testChildRequest.getId()));
        result.andExpect(jsonPath("$.clientIds[*]",containsInAnyOrder(dataset.client_1.getId().intValue())))
                .andExpect(jsonPath("$.aircraftTypeIds[*]", containsInAnyOrder(dataset.aircraft_type_1.getId().intValue())))
                .andExpect(jsonPath("$.aircraftVersionIds[*]", containsInAnyOrder(dataset.aircraft_version_1.getId().intValue())))
                .andExpect(jsonPath("$.aircraftFamilyId").value(dataset.aircraft_family_1.getId()))
                .andExpect(jsonPath("$.missionTypeId").value(dataset.mission_1.getId()))
                .andExpect(jsonPath("$.environmentId").value(dataset.environment_1.getId()))
                .andExpect(jsonPath("$.partId").value(part.getNaturalId()))
                .andExpect(jsonPath("$.isDeletable").value(true))
                .andExpect(jsonPath("$.serialNumbers[*]", containsInAnyOrder(serialNumber1, serialNumber2)))
                .andExpect(jsonPath("$.drtToInspect").value(number_of_drt_to_inspect));

    }

    private ResultActions checkPut(String endpoint) throws Exception {
        withRequest = put(endpoint).content(withBody.toString());

        return abstractCheck();
    }

    private ResultActions checkDelete(String endpoint) throws Exception {
        withRequest = delete(endpoint).content(withBody.toString());

        return abstractCheck();
    }
}

