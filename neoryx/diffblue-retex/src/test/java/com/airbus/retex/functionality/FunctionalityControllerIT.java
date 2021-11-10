package com.airbus.retex.functionality;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class FunctionalityControllerIT extends BaseControllerTest {

    private static final String API_FUNCTIONALITY = "/api/functionalities";

    @BeforeEach
    public void before() {
        dataSetInitializer.createUserFeature(FeatureCode.PART_MAPPING, dataset.user_simpleUser, EnumRightLevel.WRITE);
    }

    @Test
    public void getAllFunctionalities_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_FUNCTIONALITY);
        expectedStatus = HttpStatus.OK;

        abstractCheck()
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].name", notNullValue()))
                .andExpect(jsonPath("$[0].name", isA(String.class)));
    }
}
