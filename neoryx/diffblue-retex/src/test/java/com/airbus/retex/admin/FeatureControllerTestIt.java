package com.airbus.retex.admin;

import com.airbus.retex.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class FeatureControllerTestIt extends BaseControllerTest {

    private static final String API_FEATURES = "/api/features";

    @Test
    public void getAllFeaturesWithAllLabels_Ok() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        withParams.add("allLabels", String.valueOf(true));
        withRequest = get(API_FEATURES);

        abstractCheck()
                .andExpect(jsonPath("$[0].label", notNullValue()))
                .andExpect(jsonPath("$[0].featureCode", notNullValue()));
    }

    @Test
    public void getAllFeaturesWithOnlyUserLanguage_Ok() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        withRequest = get(API_FEATURES);
        abstractCheck()
                .andExpect(jsonPath("$[0].label", notNullValue()))
                .andExpect(jsonPath("$[0].featureCode", notNullValue()));
    }

    @Test
    public void getAllFeaturesWithSimpleUser_Forbidden() throws Exception {
        asUser = dataset.user_simpleUser2;
        expectedStatus = HttpStatus.FORBIDDEN;
        withRequest = get(API_FEATURES);
        abstractCheck();
    }

}
