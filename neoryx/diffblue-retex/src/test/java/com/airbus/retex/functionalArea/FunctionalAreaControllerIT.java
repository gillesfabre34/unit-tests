package com.airbus.retex.functionalArea;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.classification.EnumClassification;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.part.Part;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
public class FunctionalAreaControllerIT extends BaseControllerTest {

    private static final String API_FUNCTIONAL_AREAS = "/api/parts/{id}/functional-areas";
    private static final String API_GET_PART_FUNCTIONAL_AREAS = "/api/parts/{id}";

    private FunctionalArea functionalAreaOneKo;
    private FunctionalArea functionalAreaTwo;
    private FunctionalArea functionalAreaThree;

    private Part part;

    @BeforeEach
    public void before() {
        dataSetInitializer.createUserFeature(FeatureCode.PART_MAPPING, dataset.user_simpleUser, EnumRightLevel.WRITE);
         part = dataSetInitializer.createPart(null);
        functionalAreaOneKo = dataSetInitializer.createFunctionalArea(functionalArea -> {
            functionalArea.setAreaNumber("15484");
            functionalArea.setPart(part);
        });
        functionalAreaTwo = dataSetInitializer.createFunctionalArea();
        functionalAreaThree = dataSetInitializer.createFunctionalArea(functionalArea -> functionalArea.setPart(dataset.part_example_2));
        dataSetInitializer.createUserFeature(FeatureCode.PART_MAPPING, dataset.user_simpleUser, EnumRightLevel.WRITE);
    }

    @Test
    public void createFunctionalArea_ok() throws Exception {
        ObjectNode body = buildCreateFunctionalAreaBody("03");
        ArrayNode nodes=objectMapper.createArrayNode();
        nodes.add(body);
        expectedStatus = HttpStatus.NO_CONTENT;
        asUser = dataset.user_simpleUser;
        withRequest = post(API_FUNCTIONAL_AREAS,dataset.part_example.getNaturalId()).content(nodes.toString());
        abstractCheck();
    }

    @Test
    public void createFunctionalAreaWithoutTreatment_ok() throws Exception {
        ObjectNode body = buildCreateFunctionalAreaBody("03");
        body.putNull("treatmentId");
        ArrayNode nodes=objectMapper.createArrayNode();
        nodes.add(body);
        expectedStatus = HttpStatus.NO_CONTENT;
        asUser = dataset.user_simpleUser;
        withRequest = post(API_FUNCTIONAL_AREAS,dataset.part_example.getNaturalId()).content(nodes.toString());
        abstractCheck();
    }

    @Test
    public void createFunctionalAreaWithoutMaterial_ok() throws Exception {
        ObjectNode body = buildCreateFunctionalAreaBody("03");
        body.putNull("material");
        ArrayNode nodes = objectMapper.createArrayNode();
        nodes.add(body);
        expectedStatus = HttpStatus.NO_CONTENT;
        asUser = dataset.user_superAdmin;
        withRequest = post(API_FUNCTIONAL_AREAS,dataset.part_example.getNaturalId()).content(nodes.toString());
        abstractCheck();
    }

    @Test
    public void createFunctionalArea_forbidden() throws Exception {
        ObjectNode body = buildCreateFunctionalAreaBody("03");
        ArrayNode nodes = objectMapper.createArrayNode();
        nodes.add(body);
        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_simpleUser2;
        withRequest = post(API_FUNCTIONAL_AREAS,dataset.part_example_2.getNaturalId()).content(nodes.toString());
        abstractCheck();
    }

    @Test
    public void createFunctionalArea_error() throws Exception {
        ObjectNode body = buildCreateFunctionalAreaBody("03");
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_superAdmin;
        withRequest = post(API_FUNCTIONAL_AREAS,dataset.part_example.getNaturalId()).content(body.toString());
        abstractCheck();
    }

    @Test
    public void createFunctionalArea_DuplicatedAreaNumber() throws Exception {
        ArrayNode objectNode= objectMapper.createArrayNode();
        objectNode.add(buildCreateFunctionalAreaBody("03"));
        objectNode.add(buildCreateFunctionalAreaBody("03"));
        asUser = dataset.user_superAdmin;
        withRequest = post(API_FUNCTIONAL_AREAS,dataset.part_example.getNaturalId()).content(objectNode.toString());
        expectedStatus = HttpStatus.BAD_REQUEST;
        checkFunctionalException("retex.functional.area.number.already.exists");
    }

    private ObjectNode buildCreateFunctionalAreaBody(String areaNumber) {
        ObjectNode nodes = objectMapper.createObjectNode();
        nodes.put("areaNumber", areaNumber);
        nodes.put("faNameId", 1);
        nodes.put("disabled", false);
        nodes.put("functionalityId", 1);
        nodes.put("classification", "ZHS");
        nodes.put("material", "100C6");
        nodes.put("treatmentId", 1);

        return nodes;
    }

    @Test
    public void getFunctionalAreaByPartId_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_GET_PART_FUNCTIONAL_AREAS, dataset.part_example_2.getNaturalId());
        expectedStatus = HttpStatus.OK;

        abstractCheck()
                .andExpect(jsonPath("$.functionalAreas[0].areaNumber").value(functionalAreaThree.getAreaNumber()))
                .andExpect(jsonPath("$.functionalAreas[0].functionality.id").value(functionalAreaThree.getFunctionality().getId()))
                .andExpect(jsonPath("$.functionalAreas[0].functionality.name").value("Functionality - " + functionalAreaThree.getFunctionality().getId() + " (EN)"))
                .andExpect(jsonPath("$.functionalAreas[0].classification").value(EnumClassification.ZC.toString()))
                .andExpect(jsonPath("$.functionalAreas[0].functionalAreaName.id").value(functionalAreaThree.getFunctionalAreaName().getId()))
                .andExpect(jsonPath("$.functionalAreas[0].functionalAreaName.name").value(dataset.FunctionalAreaNameFr_OuterRing))
                .andExpect(jsonPath("$.functionalAreas[0].material").value(functionalAreaThree.getMaterial()))
                .andExpect(jsonPath("$.functionalAreas[0].treatment.id").value(functionalAreaThree.getTreatment().getId()))
                .andExpect(jsonPath("$.functionalAreas[0].treatment.name").value("Treatment - " + functionalAreaThree.getTreatment().getId() + " (EN)"))
                .andExpect(jsonPath("$.functionalAreas[0].disabled").value(functionalAreaThree.isDisabled()));
    }

    @Test
    public void getFunctionalAreasByPartId_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_GET_PART_FUNCTIONAL_AREAS, dataset.part_example.getNaturalId());
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$.functionalAreas[*].areaNumber", notNullValue()))
                .andExpect(jsonPath("$.functionalAreas[*].functionality", notNullValue()))
                .andExpect(jsonPath("$.functionalAreas[*].classification", notNullValue()))
                .andExpect(jsonPath("$.functionalAreas[*].functionalAreaName", notNullValue()))
                .andExpect(jsonPath("$.functionalAreas[*].material", notNullValue()))
                .andExpect(jsonPath("$.functionalAreas[*].treatment", notNullValue()))
                .andExpect(jsonPath("$.functionalAreas[*].disabled", notNullValue()));
    }

    @Test
    public void getFunctionalAreaByPartId_forbidden() throws Exception {
        asUser = dataset.user_simpleUser2;
        withRequest = get(API_GET_PART_FUNCTIONAL_AREAS, dataset.part_example.getNaturalId());
        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }

    @Test
    public void getFunctionalAreaByPartId_NotFound() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_GET_PART_FUNCTIONAL_AREAS, 0);

        expectedStatus = HttpStatus.NOT_FOUND;
        abstractCheck();
    }

    @Test
    public void putFunctionalAreaByPartIdValidationKo() throws Exception {
        ObjectNode body = buildCreateFunctionalAreaBody("2564");
        ArrayNode nodes=objectMapper.createArrayNode();
        nodes.add(body);
        asUser = dataset.user_simpleUser;
        withRequest = post(API_FUNCTIONAL_AREAS, part.getNaturalId()).content(nodes.toString());
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck();

    }
}



