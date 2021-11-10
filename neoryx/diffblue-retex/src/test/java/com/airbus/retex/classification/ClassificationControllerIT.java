package com.airbus.retex.classification;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ClassificationControllerIT extends BaseControllerTest {
    private static final String API_CLASSIFICATION = "/api/classifications";

    @BeforeEach
    public void before() {
        dataSetInitializer.createUserFeature(FeatureCode.PART_MAPPING, dataset.user_simpleUser, EnumRightLevel.WRITE);
    }

    @Test
    public void getAllClassifications_statusOk() throws Exception {
        withRequest = get(API_CLASSIFICATION);
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_simpleUser;

        abstractCheck().andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    public void getAllClassifications_Forbidden() throws Exception {
        withRequest = get(API_CLASSIFICATION);
        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_simpleUser2;
        abstractCheck();
    }
}
