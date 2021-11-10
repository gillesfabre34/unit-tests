package com.airbus.retex.functionalAreaName;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class FunctionalAreaNameControllerIT extends BaseControllerTest {
    private static final String API_FUNCTIONAL_AREA_NAMES = "/api/functional-area-names";

    @BeforeEach
    public void before() {
        dataSetInitializer.createUserFeature(FeatureCode.PART_MAPPING, dataset.user_simpleUser, EnumRightLevel.WRITE);
    }

    @Test
    public void getAllFunctionalities_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_FUNCTIONAL_AREA_NAMES);
        expectedStatus = HttpStatus.OK;
        abstractCheck().andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    public void getAllFunctionalities_Forbidden() throws Exception {
        asUser = dataset.user_simpleUser2;
        withRequest = get(API_FUNCTIONAL_AREA_NAMES);
        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }
}
