package com.airbus.retex.material;

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

public class MaterialControllerIT extends BaseControllerTest {
    private static final String API_MATERIAL = "/api/materials";

    @Autowired
    private DatasetInitializer datasetInitializer;

    @BeforeEach
    public void before() {
        dataSetInitializer.createUserFeature(FeatureCode.PART_MAPPING, dataset.user_simpleUser, EnumRightLevel.WRITE);
    }

    @Test
    public void getAllMaterials_OK() throws Exception {
        withRequest = get(API_MATERIAL);
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_simpleUser;
        abstractCheck().andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    public void getAllMaterials_forbidden() throws Exception {
        withRequest = get(API_MATERIAL);
        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_simpleUser2;
        abstractCheck();
    }
}
