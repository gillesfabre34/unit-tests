package com.airbus.retex.admin;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.messages.CommonMessages;
import com.airbus.retex.helper.ConstantsHelper;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.airbus.AirbusEntity;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.admin.RoleRepository;
import com.airbus.retex.persistence.admin.UserRepository;
import com.airbus.retex.persistence.admin.UserRoleRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Fail.fail;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


public class UserControllerIT extends BaseControllerTest {
    private static final String API_USERS = "/api/users";
    private static final String API_USER = "/api/users/{id}";
    private static final String API_USERS_REVOKE = API_USERS + "/{id}/revoke";
    private static final String API_AUTHENTICATED_USER_INFOS = API_USERS + "/me";
    private static final String API_USER_ROLES = "/api/users/{userId}/roles";
    private static final String API_USERS_ROLE = "/api/users/assignation";
    private static final String API_USER_CAN_ACCESS_FEATURE = "/api/users/{userId}/feature/access";
    private static final Long ROLE_OPERATOR = 2L;
    private static final Long ROLE_TECHNICAL_RESPONSIBLE = 3L;

    @Autowired
    private CommonMessages commonMessages;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserRepository userRepository;

    private AirbusEntity airbusEntity_usa;
    private User user_operatorOnly;
    private User user_technicalResponsibleOnly;
    private User user_operatorandTechResponsible;

    @BeforeEach
    public void before() {
        airbusEntity_usa = dataSetInitializer.createAirBusEntity("usa001", "U.S.A");
        user_operatorOnly = dataSetInitializer.createUser(airbusEntity_usa, user -> {
            user.setFirstName("User Operator");
            user.setLastName("OperatorLastName");
        });
        // file 0.7.0.xml => role 2 is operator
        //user_operatorOnly.getRoles().addOperation(roleRepository.getOne(ROLE_OPERATOR));
        dataSetInitializer.createUserRole(roleRepository.getOne(ROLE_OPERATOR), user_operatorOnly);
        user_technicalResponsibleOnly = dataSetInitializer.createUser(airbusEntity_usa, user -> {
            user.setFirstName("Technical Responsible");
            user.setLastName("TechRespLastName");

        });
        // file 0.7.0.xml => role 3 is technical responsible
        //user_technicalResponsibleOnly.getRoles().addOperation(roleRepository.getOne(ROLE_TECHNICAL_RESPONSIBLE));
        dataSetInitializer.createUserRole(roleRepository.getOne(ROLE_TECHNICAL_RESPONSIBLE), user_technicalResponsibleOnly);
        user_operatorandTechResponsible = dataSetInitializer.createUser(airbusEntity_usa, user -> {
            user.setFirstName("User Operator and Technical Responsible");
            user.setLastName("OperatorAndTechRespLastName");
        });
        //user_operatorandTechResponsible.getRoles().addOperation(roleRepository.getOne(ROLE_OPERATOR));
        //user_operatorandTechResponsible.getRoles().addOperation(roleRepository.getOne(ROLE_TECHNICAL_RESPONSIBLE));
        dataSetInitializer.createUserRole(roleRepository.getOne(ROLE_OPERATOR), user_operatorandTechResponsible);
        dataSetInitializer.createUserRole(roleRepository.getOne(ROLE_TECHNICAL_RESPONSIBLE), user_operatorandTechResponsible);
    }

    @Test
    public void getAllUsersByIdAirbusEntity_statusok() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_USERS)
                .param("airbusEntityId", dataset.airbusEntity_france.getId().toString());
        expectedStatus = HttpStatus.OK;

        abstractCheck()
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.totalResults").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results[0].id").isNumber())
                .andExpect(jsonPath("$.results[0].firstName").isString())
                .andExpect(jsonPath("$.results[0].lastName").isString())
                .andExpect(jsonPath("$.results[0].email").isString())
                .andExpect(jsonPath("$.results[0].staffNumber").isString())
                .andExpect(jsonPath("$.results[0].language").isString())
                .andExpect(jsonPath("$.results[0].state").isString())
                .andExpect(jsonPath("$.results[0].roles").isArray());
        //FIXME .andExpect(jsonPath("$.results[0].roles[0].id").isNumber())
        //FIXME .andExpect(jsonPath("$.results[0].roles[0].label").isString());
    }

    @Test
    public void getAllUsersByIdAirbusEntityAndActive_statusok() throws Exception {

        /**
         * Get all active users with airbus entity id is 1
         */

        withParams.add("airbusEntityId", String.valueOf(dataset.airbusEntity_france.getId()));
        withParams.add("onlyActive", "true");
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_superAdmin;
        withRequest = get(API_USERS);

        abstractCheck()
                .andExpect(jsonPath("$.results[0].state").value(EnumActiveState.ACTIVE.toString()));
    }

    @Test
    public void getAllUsersByIdAirbusEntity_user_per_page_ok() throws Exception {

        /**
         * Get all users with pagination
         */

        withParams.add("airbusEntityId", String.valueOf(dataset.airbusEntity_france.getId()));
        withParams.add("size", "1");
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_superAdmin;
        withRequest = get(API_USERS);

        abstractCheck()
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results", hasSize(1)));
    }

    @Test
    public void getAllUsersByIdAirbusEntity_search_on_userName_ok_noResults() throws Exception {
        final String keyword = "kdhfirtn";

        withParams.add("airbusEntityId", String.valueOf(dataset.airbusEntity_france.getId()));
        withParams.add("search", keyword);
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_superAdmin;
        withRequest = get(API_USERS);

        abstractCheck()
                .andExpect(jsonPath("$.results", hasSize(0)));
    }

    @Test
    public void getAllUsersByIdAirbusEntity_search_on_userName_ok() throws Exception {
        final String keyword = "kdhfirtn";
        dataSetInitializer.createUser(user -> user.setFirstName(keyword));
        dataSetInitializer.createUser(user -> user.setLastName(keyword.toUpperCase()));

        withParams.add("airbusEntityId", String.valueOf(dataset.airbusEntity_france.getId()));
        withParams.add("search", keyword);
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_superAdmin;
        withRequest = get(API_USERS);

        abstractCheck()
                .andExpect(jsonPath("$.results", hasSize(2)));
    }

    @Test
    public void getAllUsersByIdAirbusEntity_search_on_staffNumber_ok() throws Exception {
        /**
         * Get users containing search text "FR000001"
         */
        withParams.add("airbusEntityId", String.valueOf(dataset.airbusEntity_france.getId()));
        withParams.add("search", dataset.user_simpleUser2.getStaffNumber());
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_superAdmin;
        withRequest = get(API_USERS);

        abstractCheck()
                .andExpect(jsonPath("$.results[0].staffNumber", containsString(dataset.user_simpleUser2.getStaffNumber())));
    }

    @Test
    public void getAllUsersByIdAirbusEntity_search_on_firstName_lastName_ok() throws Exception {
        /**
         * Get users containing search text "jean dupont"
         */
        withParams.add("airbusEntityId", String.valueOf(dataset.airbusEntity_france.getId()));
        withParams.add("search", "jean dupont");
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_superAdmin;
        withRequest = get(API_USERS);

        abstractCheck()
                .andExpect(jsonAt("$.results[0]",
                        andAt(".firstName", containsString("jean")),
                        andAt(".lastName", containsString("dupont"))));
    }


    @Test
    public void getAuthenticatedUserInfos_Ok() throws Exception {
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_superAdmin;
        withRequest = get(API_AUTHENTICATED_USER_INFOS);

        List<String> codes = Stream.of(FeatureCode.values()).map(Enum::name).collect(Collectors.toList());

        abstractCheck()
                .andExpect(jsonPath("$.firstName", equalTo(asUser.getFirstName())))
                .andExpect(jsonPath("$.lastName", equalTo(asUser.getLastName())))
                .andExpect(jsonPath("$.email", equalTo(asUser.getEmail())))
                .andExpect(jsonPath("$.features", hasSize(FeatureCode.values().length)))
                .andExpect(jsonPath("$.features[*].code", containsInAnyOrder(codes.toArray())))
                .andExpect(jsonPath("$.features[*].rightLevel", everyItem(equalTo(EnumRightLevel.WRITE.toString()))));
    }


    @Test
    public void createUser_ok() throws Exception {
        /**
         * registrer post_a new User
         */

        ObjectNode nodes = objectMapper.createObjectNode();
        nodes.put("airbusEntityId", dataset.airbusEntity_france.getId());
        nodes.put("email", "testcreateuser@gmail.com");
        nodes.put("firstName", "testcreateuser");
        nodes.put("lastName", "testcreateuser");
        nodes.put("language", "FR");
        nodes.put("staffNumber", "Z999999");
        nodes.put("isOperator", false);
        nodes.put("isTechnicalResponsible", false);
        nodes.putArray("roleIds").add(dataset.role_admin.getId());
        nodes.putArray("features")
                .addObject().put("code", "ADMIN")
                .put("rightLevel", "READ");

        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_superAdmin;
        withRequest = post(API_USERS).content(objectMapper.writeValueAsBytes(nodes));

        abstractCheck()
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("testcreateuser"))
                .andExpect(jsonPath("$.lastName").value("testcreateuser"))
                .andExpect(jsonPath("$.email").value("testcreateuser@gmail.com"))
                .andExpect(jsonPath("$.staffNumber").value("Z999999"))
                .andExpect(jsonPath("$.language").value("FR"))
                .andExpect(jsonPath("$.state").value(EnumActiveState.ACTIVE.toString()))
                .andExpect(jsonPath("$.airbusEntityId").value(dataset.airbusEntity_france.getId()))
                .andExpect(jsonAt("$.roles[0]",
                        andAt(".id", equalTo(dataset.role_admin.getId().intValue())),
                        andAt(".label", not(isEmptyOrNullString()))))
                .andExpect(jsonAt("$.features[0]",
                        andAt(".code", equalTo("ADMIN")),
                        andAt(".rightLevel", equalTo("READ"))));
    }

    @Test
    public void getUser_NotFound() throws Exception {
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_superAdmin;
        withRequest = get(API_USER, "99999");
        checkFunctionalException("retex.user.not.found.label");
    }

    @Test
    public void getUser_Forbidden() throws Exception {
        /**
         * Try to find an inexisting user
         */
        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_simpleUser;
        withRequest = get(API_USER, dataset.user_simpleUser.getId());
        abstractCheck();
    }

    @Test
    public void getUser_ok() throws Exception {
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_superAdmin;

        withRequest = get(API_USER, dataset.user_simpleUser.getId().toString());
        abstractCheck()
                .andExpect(jsonPath("$.id").value(dataset.user_simpleUser.getId()))
                .andExpect(jsonPath("$.airbusEntityId").value(dataset.airbusEntity_canada.getId()))
                .andExpect(jsonPath("$.firstName").value(dataset.user_simpleUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(dataset.user_simpleUser.getLastName()))
                .andExpect(jsonPath("$.language").value(dataset.user_simpleUser.getLanguage().name()))
                .andExpect(jsonPath("$.staffNumber").value(dataset.user_simpleUser.getStaffNumber()))
                .andExpect(jsonPath("$.state").value(dataset.user_simpleUser.getState().toString()))
                .andExpect(jsonPath("$.email").value(dataset.user_simpleUser.getEmail()))

                //FIXME : LE TEST NE PASSE PAS : $.roles is null
                //.andExpect(jsonPath("$.roles").isArray())

                .andExpect(jsonPath("$.features").isArray());

        //FIXME : LE TEST NE PASSE PAS : No value at JSON path "$.roles[0].id"
        // .andExpect(jsonPath("$.roles[0].id").value(dataset.role_simpleRole.getNaturalId()))

        // FIXME : LE TEST NE PASSE PAS : verify value of the label
        //.andExpect(jsonPath("$.roles[0].label").value(dataset.simpleRoleFrLabel.getValue()))

        //FIXME .andExpect(jsonPath("$.features[0].id").value(dataset.simpleFeature.getNaturalId()))
        //FIXME .andExpect(jsonPath("$.features[0].label").value(dataset.simpleFeatureFrLabel.getValue()));
    }


    //    @Test
//TODO ce test est il utile sachant qu on suppose que l'utilisateur existe en base pour l'update
    public void updateUser_notFound() throws Exception {
        /**
         * Try to update an inexisting user
         */
        expectedStatus = HttpStatus.NOT_FOUND;
        asUser = dataset.user_superAdmin;
        ObjectNode nodes = objectMapper.createObjectNode();
        nodes.put("isoCode", ConstantsHelper.FR_ISO_CODE);
        nodes.put("roleIds", objectMapper.createArrayNode().add(dataset.role_admin.getId()));
        //FIXME nodes.putArray("userFeatures").add(objectMapper.readTree("{ \"featureId\": " + dataset.simpleFeature.getNaturalId()
        //FIXME         + ", \"rightLevel\": \"" + EnumRightLevelDto.READ.toString() + "\"}"));
        withRequest = put(API_USER, "-1").content(objectMapper.writeValueAsBytes(nodes));

        try {
            abstractCheck();
            fail("exception should occur : user not found");
        } catch (Exception e) {
            assertTrue(e.getCause().getLocalizedMessage().contains("Unable to find com.airbus.retex.model.user.User"));
        }
    }


    @Test
    public void updateUser_bodyEmpty() throws Exception {
        /**
         * Try to update post_a user without body
         */
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_superAdmin;
        withRequest = put(API_USER, "2");
        abstractCheck();
    }

    //@Test
    public void updateUser_forbidden() throws Exception {
        /**
         * Try to update an inexisting user
         * TODO secure Admin
         */
        ObjectNode nodes = objectMapper.createObjectNode();
        nodes.put("isoCode", ConstantsHelper.FR_ISO_CODE);
        nodes.put("roleIds", objectMapper.createArrayNode().add(dataset.role_operator.getId()));
        //FIXME nodes.putArray("userFeatures").add(objectMapper.readTree("{ \"featureId\": " + dataset.simpleFeature.getNaturalId()
        //FIXME         + ", \"rightLevel\": \"" + EnumRightLevelDto.READ.toString() + "\"}"));


        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_simpleUser;
        withRequest = put(API_USER, "1")
                .content(objectMapper.writeValueAsBytes(nodes));

        abstractCheck();
    }


    @Test
    public void updateUser_ok() throws Exception {
        int[] intRoleIds = new int[]{dataset.role_admin.getId().intValue(),
                dataset.role_technical_responsible.getId().intValue()};

        ObjectNode nodes = objectMapper.createObjectNode();
        nodes.put("language", Language.FR.name());
        nodes.put("roleIds", objectMapper.createArrayNode().add(intRoleIds[0]).add(intRoleIds[1]));
        nodes.putArray("features").addObject()
                .put("code", "ADMIN")
                .put("rightLevel", "WRITE");

        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_superAdmin;
        withRequest = put(API_USER, dataset.user_simpleUser.getId()).content(nodes.toString());

        abstractCheck()
                .andExpect(jsonPath("$.id").value(dataset.user_simpleUser.getId()))
                .andExpect(jsonPath("$.firstName").value(dataset.user_simpleUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(dataset.user_simpleUser.getLastName()))
                .andExpect(jsonPath("$.language").value(dataset.user_simpleUser.getLanguage().name()))
                .andExpect(jsonPath("$.staffNumber").value(dataset.user_simpleUser.getStaffNumber()))
                .andExpect(jsonPath("$.state").value(dataset.user_simpleUser.getState().toString()))
                .andExpect(jsonPath("$.email").value(dataset.user_simpleUser.getEmail()))
                .andExpect(jsonAt("$.roles[*]",
                        andAt(".id", hasItems(intRoleIds[0], intRoleIds[1])),
                        andAt(".label", not(isEmptyOrNullString()))))
                .andExpect(jsonAt("$.features[*]",
                        andAt("code", hasItem("ADMIN")),
                        andAt("rightLevel", hasItem("WRITE"))));
    }

    @Test
    public void revokeUser_ok() throws Exception {
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_superAdmin;
        withRequest = put(API_USERS_REVOKE, dataset.user_superAdmin.getId());
        abstractCheck().andExpect(jsonPath("$.state", equalToIgnoringCase(EnumActiveState.REVOKED.name())));
    }

    //FIXME addOperation test with user that can't revoke post_a user
    @Test
    public void revokeUser_forbidden() {
    }

    /**
     * Test Scenario : All params are optionals.
     *
     * @throws Exception
     */
    @Test
    public void getAllUsersNoParam_statusok() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_USERS);
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.totalResults").isNumber());
    }

    /**
     * Test Scenario : Find only users having role OPERATOR
     *
     * @throws Exception
     */
    @Test
    public void getAllUsersOperator_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("roleCode", RoleCode.INTERNAL_OPERATOR.name());
        withRequest = get(API_USERS);
        expectedStatus = HttpStatus.OK;

        check(4);
    }

    /**
     * Test Scenario : Find only users having role TECHNICAL RESPONSIBLE
     *
     * @throws Exception
     */
    @Test
    public void getAllUsersTechnicalResponsable_statusok() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("roleCode", RoleCode.TECHNICAL_RESPONSIBLE.name());
        withRequest = get(API_USERS);
        expectedStatus = HttpStatus.OK;
        check(3);
    }

    @Test
    public void getAllUsersByRoleOperator_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.READ);
        withParams.add("roleCode", RoleCode.INTERNAL_OPERATOR.name());
        withRequest = get(API_USERS_ROLE);
        expectedStatus = HttpStatus.OK;

        check(4);
    }

    @Test
    public void getAllUsersByRoleTechnicalResponsable_Ok() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.READ);
        withParams.add("roleCode", RoleCode.TECHNICAL_RESPONSIBLE.name());
        withRequest = get(API_USERS_ROLE);
        expectedStatus = HttpStatus.OK;
        check(3);
    }

    @Test
    public void getAllUsersByRoleOperator_Forbidden() throws Exception {
        asUser = dataset.user_simpleUser2;
        withParams.add("roleCode", RoleCode.INTERNAL_OPERATOR.name());
        withRequest = get(API_USERS_ROLE);
        expectedStatus = HttpStatus.FORBIDDEN;

        abstractCheck();
    }

    @Test
    public void getAllUsersByRoleTechnicalResponsable_Forbidden() throws Exception {
        asUser = dataset.user_simpleUser2;
        withParams.add("roleCode", RoleCode.TECHNICAL_RESPONSIBLE.name());
        withRequest = get(API_USERS_ROLE);
        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }


    /**
     * Test Scenario : Find only by isOperator=TRUE isTechnicalResponsible=FALSE Filter.
     *
     * @throws Exception
     */
    //FIXME
    //@Test
    public void getAllUsersOperatorButNotTechnicalResponsible_statusok() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("isOperator", "true");
        withParams.add("isTechnicalResponsible", "false");
        withRequest = get(API_USERS);
        expectedStatus = HttpStatus.OK;

        check(1);
    }

    /**
     * Test Scenario : Find only by isOperator=FALSE isTechnicalResponsible=TRUE Filter.
     *
     * @throws Exception
     */
    //FIXME
    //@Test
    public void getAllUsersTechnicalResponsibleButNotOperator_statusok() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("isOperator", "false");
        withParams.add("isTechnicalResponsible", "true");
        withRequest = get(API_USERS);
        expectedStatus = HttpStatus.OK;

        check(1);
    }

    /**
     * Test Scenario : Find only by isOperator=FALSE and airbusentity is USA Filter.
     *
     * @throws Exception
     */
    //FIXME
    //@Test
    public void getAllUsersNotOperatorFromUSA_statusok() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("isOperator", "false");
        withParams.add("airbusEntityId", airbusEntity_usa.getId().toString());
        withRequest = get(API_USERS);
        expectedStatus = HttpStatus.OK;
        check(1);

    }

    /**
     * Test Scenario : Find only by isTechnicalResponsible=FALSE and airbusentity is USA Filter.
     *
     * @throws Exception
     */
    //FIXME
    //@Test
    public void getAllUsersNotTechnicalResponsibleFromUSA_statusok() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("isTechnicalResponsible", "false");
        withParams.add("airbusEntityId", airbusEntity_usa.getId().toString());
        withRequest = get(API_USERS);
        expectedStatus = HttpStatus.OK;
        check(5);
    }

    private void check(final int totalResults) throws Exception {
        abstractCheck().andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.totalResults", equalTo(totalResults)));
    }

    @Test
    public void getUserRoles_ok() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_USER_ROLES, dataset.user_with_one_role.getId());
        expectedStatus = HttpStatus.OK;
        abstractCheck().andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(dataset.user_with_one_role.getRoles().size())));
    }

    @Test
    public void getUserRoles_user_not_found() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_USER_ROLES, 99999);
        expectedStatus = HttpStatus.BAD_REQUEST;
        checkFunctionalException("retex.user.not.found.label");
    }

    @Test
    public void getUserRoles_has_no_role() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_USER_ROLES, dataset.user_without_role.getId());
        expectedStatus = HttpStatus.OK;
        abstractCheck().andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // TODO : FIXME :  UserService.getUserFeatureRights
//    @Test
//    public void userCanAccessFeature_can_access() throws Exception {
//        withParams.add("feature", FeatureCode.ROUTING.name());
//        withParams.add("right", EnumRightLevel.READ.name());
//        expectedStatus = HttpStatus.OK;
//        checkRequestAccessFeature(true);
//    }
//
//    @Test
//    public void userCanAccessFeature_cannot_access() throws Exception {
//        withParams.add("feature", FeatureCode.ADMIN.name());
//        withParams.add("right", EnumRightLevel.WRITE.name());
//        expectedStatus = HttpStatus.OK;
//        checkRequestAccessFeature(false);
//    }
//
//    private void checkRequestAccessFeature(boolean expectedResult) throws Exception{
//        asUser = dataset.user_superAdmin;
//        withRequest = get(API_USER_CAN_ACCESS_FEATURE, dataset.user_with_one_role.getId());
//        abstractCheck().andExpect(jsonPath("$").isBoolean())
//                .andExpect(jsonPath("$", is(expectedResult)));
//    }

}