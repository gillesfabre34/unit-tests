package com.airbus.retex.admin;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class GetAuthenticatedUserInfosControllerIT extends BaseControllerTest {

    private static final String ENDPOINT_URL = "/api/users/me";

    @Test
    public void asAdmin_ok() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;

        check()
            .andExpect(jsonPath("$.id", equalTo(asUser.getId().intValue())))
            .andExpect(jsonPath("$.firstName", equalTo(asUser.getFirstName())))
            .andExpect(jsonPath("$.lastName", equalTo(asUser.getLastName())))
            .andExpect(jsonPath("$.email", equalTo(asUser.getEmail())))
            .andExpect(jsonPath("$.features", notNullValue()));
    }

    @Test
    public void asAnonymous_unauthorized() throws Exception {
        expectedStatus = HttpStatus.UNAUTHORIZED;
        check();
    }


    @Test
    public void asAdmin_hasAllFeatureWrite() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;

        String[] featureCodes = Stream.of(FeatureCode.values())
                .map(Enum::name)
                .collect(Collectors.toList())
                .toArray(new String[]{});
        int featureCount = FeatureCode.values().length;
        check()
            .andExpect(jsonPath("$.features",  hasSize(featureCount)))
            .andExpect(jsonPath("$.features[*].code",  hasSize(featureCount)))
            .andExpect(jsonPath("$.features[*].code",  containsInAnyOrder(featureCodes)))
            .andExpect(jsonPath("$.features[*].rightLevel",  hasSize(featureCount)))
            .andExpect(jsonPath("$.features[*].rightLevel",  everyItem(equalTo(EnumRightLevel.WRITE.name()))));
    }


    @Test
    public void asSimpleUser_hasNoFeature() throws Exception {
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.OK;

        check().andExpect(jsonPath("$.features",  hasSize(0)));
    }


    @Test
    public void asSimpleUser_hasOneUserFeature() throws Exception {
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.READ);

        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.OK;

        check().andExpect(jsonPath("$.features", hasItem(allOf(
                    hasEntry("code", FeatureCode.ROUTING.name()),
                    hasEntry("rightLevel", EnumRightLevel.READ.name())
                ))));
    }

    private ResultActions check() throws Exception {
        withRequest = get(ENDPOINT_URL);

        return abstractCheck();
    }
}
