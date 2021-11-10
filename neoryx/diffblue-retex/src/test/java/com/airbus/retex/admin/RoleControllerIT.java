package com.airbus.retex.admin;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.LightDto;
import com.airbus.retex.business.dto.enumeration.EnumRightLevelDto;
import com.airbus.retex.business.dto.feature.FeatureRightDto;
import com.airbus.retex.business.dto.role.RoleCreationUpdateDto;
import com.airbus.retex.business.dto.role.RoleFieldEnum;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.persistence.admin.RoleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class RoleControllerIT extends BaseControllerTest {

    @Autowired
    private RoleRepository roleRepository;
    private static final String API_ROLES = "/api/roles";
    private static final String API_ROLES_REVOKE = API_ROLES + "/revoke";
    private static final String API_ROLES_SEGREGATION = API_ROLES + "/segregation";
    private static final String API_ROLE = API_ROLES + "/{id}";
    private static final String API_ROLE_UPDATE = API_ROLES + "/{id}";

    public RoleControllerIT() {
    }

    @Test
    void getAllRolesLabels_OK() throws Exception {
        withRequest = get(API_ROLES + "?airbusEntityId=" + 1);
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        abstractCheck();
    }

    @Test
    void getRole_OK() throws Exception {
        withRequest = get(API_ROLE, 1);
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        abstractCheck().andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.airbusEntity.id", notNullValue()))
                .andExpect(jsonPath("$.features[0]", notNullValue()))
                .andExpect(jsonPath("$.translatedLabels", notNullValue()));
    }

    // FIXME : test exception catched from service (NOT_FOUND)
    @Test
    void getRole_NotFound() throws Exception {

    }

    //FIXME @Test
    void updateRole_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        ObjectNode nodes = objectMapper.createObjectNode();

        nodes.put("airbusEntity", dataset.airbusEntity_france.getId());
        // construct our object RoleUpdateDto json
        nodes.putArray("features")
                .add(objectMapper.readTree("{ \"code\" : \"" + FeatureCode.ADMIN.name() + "\""
                        + " , \"rightLevel\": \"" + EnumRightLevelDto.READ.toString() + "\"}"))
                .add(objectMapper.readTree("{ \"code\" : \"" + FeatureCode.ADMIN.name() + "\""
                        + " , \"rightLevel\": \"" + EnumRightLevelDto.WRITE.toString() + "\"}"));
        nodes.putObject("translatedLabels")
                .put("FR", "Nouveau label francais")
                .put("EN", "New english label");
        // run request
        withRequest = put(API_ROLE_UPDATE, 1).content(objectMapper.writeValueAsBytes(nodes));
        abstractCheck()
                .andExpect(jsonPath("$.id").value(dataset.user_superAdmin.getId()))
                .andExpect(jsonPath("$.airbusEntity.id").value(dataset.airbusEntity_france.getId()))
                .andExpect(jsonPath("$.features[0]", notNullValue()))
                .andExpect(jsonPath("$.translatedLabels", notNullValue()));
    }


    //FIXME @Test
    void updateRoleForAnOtherEntity_FORBIDDEN() throws Exception {
        ObjectNode body = objectMapper.createObjectNode().put("id", dataset.role_admin.getId());
        JsonNode airbusEntity = objectMapper.createObjectNode().put("id", 14L);
        body.set("airbusEntity", airbusEntity);
        body.putArray("features")
                .add(objectMapper.readTree("{ \"featureId\": " + 1
                        + ", \"rightLevel\": \"" + EnumRightLevel.READ.toString() + "\"}"))
                .add(objectMapper.readTree("{ \"featureId\": " + 2
                        + ", \"rightLevel\": \"" + EnumRightLevel.WRITE.toString() + "\"}"));

        asUser = dataset.user_simpleUser;
        withRequest = put(API_ROLE_UPDATE, 1).content(body.toString());
        expectedStatus = HttpStatus.FORBIDDEN;

        abstractCheck();
    }

    @Test
    void getAllRolesWithUserIsoCode_statusok() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        withParams.add("allLabels", "false");
        withRequest = get(API_ROLES_SEGREGATION).params(withParams);

        abstractCheck();
        //FIXME .andExpect(jsonPath("$", hasSize(2)))
        //FIXME .andExpect(jsonPath("$[1].label").isString());
        // FIXME .andExpect(jsonPath("$[1].label", equalTo("Role number 2")));
    }

    //FIXME @Test
    void getAllRolesWithAllLabels_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        withParams.add("allLabels", "true");
        withRequest = get(API_ROLES_SEGREGATION);

        abstractCheck()
                .andExpect(jsonPath("$", hasSize((int) roleRepository.count())))
                .andExpect(jsonPath("$[1].label").isString());
    }

    //FIXME @Test
    void getAllRolesWithAllLabels_With_AirbusEntityID_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        withParams.add("allLabels", "true");
        withRequest = get(API_ROLES_SEGREGATION + "?airbusEntityId=" + 1).locale(new Locale("EN"));
        abstractCheck()
                // FIXME : remettre plus tard
                //.andExpect(jsonPath("$", hasSize((int) roleRepository.count()) )  )
                .andExpect(jsonPath("$[1].label").isString());
    }

    @Test
    void getAllRolesWithSpecifiqueAirbusEntity_statusok() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        withParams.add("allLabels", "true");
        withParams.add("airbusEntityId", "0");
        withRequest = get(API_ROLES_SEGREGATION + "?airbusEntityId=" + 0);

        abstractCheck().andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllRoles_statusIsOk() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        withRequest = get(API_ROLES + "?airbusEntityId=" + 1);

        abstractCheck();
    }

    @Test
    void revokeRoleWith_statusIsOk() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("roleId", String.valueOf(1L));
        expectedStatus = HttpStatus.OK;
        withRequest = put(API_ROLES_REVOKE);
        checkResponseContentType = false;

        abstractCheck();
    }

    @Test
    void revokeRole_statusForbidden() throws Exception {
        asUser = dataset.user_simpleUser;
        withParams.add("roleId", String.valueOf(1L));
        withRequest = put(API_ROLES_REVOKE);
        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }

    @Test
    void createRole_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;

        RoleCreationUpdateDto roleCreationDto=new RoleCreationUpdateDto();
        roleCreationDto.setRoleCode(RoleCode.ADMIN);
        roleCreationDto.setAirbusEntity(new LightDto( dataset.airbusEntity_france.getId()));

        List<FeatureRightDto> features = new ArrayList<>();
        FeatureRightDto featureAdminNone = new FeatureRightDto();
        featureAdminNone.setCode(FeatureCode.ADMIN);
        featureAdminNone.setRightLevel(EnumRightLevel.WRITE);
        features.add(featureAdminNone);
        roleCreationDto.setFeatures(features);

        Map<Language, Map<RoleFieldEnum, String>> translates = new HashMap<>();
        translates.put(Language.FR, Map.of(RoleFieldEnum.label, "Nom du role"));
        translates.put(Language.EN, Map.of(RoleFieldEnum.label, "Name of the role"));
        roleCreationDto.setTranslatedFields(translates);

        withRequest = post(API_ROLES).content(objectMapper.writeValueAsBytes(roleCreationDto));
        abstractCheck().andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.airbusEntity.id").value(dataset.airbusEntity_france.getId().intValue()))
                .andExpect(jsonAt("$.features[0]",
                        andAt(".code", equalTo("ADMIN")),
                        andAt(".rightLevel", equalTo("WRITE"))))
                .andExpect(jsonPath("$.translatedLabels", notNullValue()));
    }

    //FIXME @Test
    void createRoleWithExistedLabel_INTERNAN_ERROR() throws Exception {
        RoleCreationUpdateDto roleCreationDto = new RoleCreationUpdateDto();
        // this id already exists
        roleCreationDto.setAirbusEntity(new LightDto(1L));
        //RoleFeatureCreationDto featureRoleCreationDto = new RoleFeatureCreationDto(EnumRightLevelDto.WRITE, new LightDto(1L));
        //roleCreationDto.setFeatures(Arrays.asList(featureRoleCreationDto));
        // FIXME
        // List<FieldTranslateDto> translatedLabels = new ArrayList<>();
        // translatedLabels.addOperation(new FieldTranslateDto(Language.EN, "Role number " + 1));
        //roleCreationDto.setTranslatedLabels(translatedLabels);
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.BAD_REQUEST;
        withRequest = post(API_ROLES).content(objectMapper.writeValueAsBytes(roleCreationDto));//FIXME create the JSON by hands
        ResultActions result = abstractCheck();
        abstractCheck().andExpect(jsonPath("$.messages[0]", equalTo("the labels chosen for this role already exists")));
    }

    @Test
    void createRoleWithEmptyList_statusBadRequest() throws Exception {
//        RoleCreationDto roleCreationDto = new RoleCreationDto();
//
//        roleCreationDto.setAirbusEntity(new LightDto(1L));
//        FeatureRoleCreationDto featureRoleCreationDto = new FeatureRoleCreationDto(EnumRightLevelDto.WRITE, new LightDto(1L));
//
//        // roleCreationDto.setRoleLabels(Arrays.asList());
//
//        roleCreationDto.setFeatureRoles(Arrays.asList(featureRoleCreationDto));
//
//        asUser = dataset.user_superAdmin;
//        expectedStatus = HttpStatus.BAD_REQUEST;
//        withRequest = post(API_ROLES).content(objectMapper.writeValueAsBytes(roleCreationDto));
//
//        abstractCheck();
    }


}
