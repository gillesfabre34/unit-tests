package com.airbus.retex.aicraft;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.persistence.aircraft.AircraftFamilyRepository;
import com.airbus.retex.persistence.aircraft.AircraftTypeRepository;
import com.airbus.retex.persistence.aircraft.AircraftVersionRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class AircraftControllerIT extends BaseControllerTest {

    private static final String API_AIRCRAFTS_FAMILIES = "/api/aircraft-families";
    private static final String API_AIRCRAFTS_TYPES = "/api/aircraft-types";
    private static final String API_AIRCRAFTS_VERSIONS = "/api/aircraft-versions";


    @Autowired
    private AircraftFamilyRepository aircraftFamilyRepository;

    @Autowired
    private AircraftTypeRepository aircraftTypeRepository;

    @Autowired
    private AircraftVersionRepository aircraftVersionRepository;

    @Test
    void getAllFamilies_Ok() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        withRequest=get(API_AIRCRAFTS_FAMILIES);
        abstractCheck()
                .andExpect(jsonPath("$", hasSize((int) aircraftFamilyRepository.count())))
                .andExpect(jsonPath("$[*]", notNullValue()));

    }

    @Test
    void getAllFamilies_Forbidden() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_AIRCRAFTS_FAMILIES);
        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }


    @Test
    public void getAllTypes_FamilyFiltered_Ok_1() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("aircraftFamily", dataset.aircraft_set_family_2.getId().toString());
        expectedStatus = HttpStatus.OK;
        withRequest=get(API_AIRCRAFTS_TYPES);
        ResultActions res = abstractCheck();
        res.andExpect(jsonPath("$[*].name", containsInAnyOrder(
                dataset.aircraft_set_family_2_type_1.getName()
        )));
    }

    @Test
    public void getAllTypes_FamilyFiltered_Ok_2() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("aircraftFamily", dataset.aircraft_set_family_1.getId().toString());
        expectedStatus = HttpStatus.OK;
        withRequest=get(API_AIRCRAFTS_TYPES);
        ResultActions res = abstractCheck();
        res.andExpect(jsonPath("$[*].name", containsInAnyOrder(
                dataset.aircraft_set_family_1_type_1.getName(),
                dataset.aircraft_set_family_1_type_2.getName()
        )));
    }


    @Test
    public void getAllVersions_TypeFiltered_Ok_1() throws Exception {
        asUser = dataset.user_superAdmin;
        ObjectNode withBody = objectMapper.createObjectNode();
        withBody.putArray("aircraftTypeIds").add(dataset.aircraft_set_family_2_type_1.getId().toString());
        expectedStatus = HttpStatus.OK;
        withRequest = post(API_AIRCRAFTS_VERSIONS).content(withBody.toString());
        ResultActions res = abstractCheck();
        res.andExpect(jsonPath("$[*].name", containsInAnyOrder(
                dataset.aircraft_set_family_2_type_1_version_1.getName()
        )));
    }

    @Test
    public void getAllVersions_TypeFiltered_Ok_2() throws Exception {
        asUser = dataset.user_superAdmin;
        ObjectNode withBody = objectMapper.createObjectNode();
        withBody.putArray("aircraftTypeIds").add(dataset.aircraft_set_family_1_type_1.getId().toString());
        expectedStatus = HttpStatus.OK;
        withRequest = post(API_AIRCRAFTS_VERSIONS).content(withBody.toString());
        ResultActions res = abstractCheck();
        res.andExpect(jsonPath("$[*].name", containsInAnyOrder(
                dataset.aircraft_set_family_1_type_1_version_2.getName(),
                dataset.aircraft_set_family_1_type_1_version_1.getName()
        )));
    }

    @Test
    public void getAllVersions_TypeFiltered_Ok_3() throws Exception {
        asUser = dataset.user_superAdmin;
        ObjectNode withBody = objectMapper.createObjectNode();
        withBody.putArray("aircraftTypeIds")
                .add(dataset.aircraft_set_family_1_type_1.getId().toString())
                .add(dataset.aircraft_set_family_1_type_2.getId().toString());;
        expectedStatus = HttpStatus.OK;
        withRequest = post(API_AIRCRAFTS_VERSIONS).content(withBody.toString());
        ResultActions res = abstractCheck();
        res.andExpect(jsonPath("$[*].name", containsInAnyOrder(
                dataset.aircraft_set_family_1_type_1_version_2.getName(),
                dataset.aircraft_set_family_1_type_2_version_1.getName(),
                dataset.aircraft_set_family_1_type_1_version_1.getName()
        )));
    }

    @Test
    public void getAllVersions_TypeFiltered_Ok_4() throws Exception {
        asUser = dataset.user_superAdmin;
        ObjectNode withBody = objectMapper.createObjectNode();
        withBody.putArray("aircraftTypeIds");
        expectedStatus = HttpStatus.OK;
        withRequest = post(API_AIRCRAFTS_VERSIONS).content(withBody.toString());
        ResultActions res = abstractCheck();
        res.andExpect(jsonPath("$[*]", hasSize(0)));
    }


    @Test
    public void getAllVersions_oldEndpoint_Ok() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("aircraftType", dataset.aircraft_set_family_1_type_1.getId().toString());
        expectedStatus = HttpStatus.OK;
        withRequest=get(API_AIRCRAFTS_VERSIONS);
        ResultActions res = abstractCheck();
        res.andExpect(jsonPath("$[*].name", containsInAnyOrder(
                dataset.aircraft_set_family_1_type_1_version_2.getName(),
                dataset.aircraft_set_family_1_type_1_version_1.getName()
        )));
    }

}
