package com.airbus.retex.treatment;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.dataset.DatasetInitializer;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class TreatmentControllerIT extends BaseControllerTest {
    private static final String API_TREATMENT = "/api/treatments";

    @Autowired
    private DatasetInitializer datasetInitializer;

    @BeforeEach
    public void before() {
        dataSetInitializer.createUserFeature(FeatureCode.PART_MAPPING, dataset.user_simpleUser, EnumRightLevel.WRITE);
    }

    @Test
    public void getAllTreatments_statusOk() throws Exception {
        withRequest = get(API_TREATMENT);
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_simpleUser;

        abstractCheck().andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    public void getAllTreatments_forbidden() throws Exception {
        withRequest = get(API_TREATMENT);
        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_simpleUser2;

        abstractCheck();
    }
}
