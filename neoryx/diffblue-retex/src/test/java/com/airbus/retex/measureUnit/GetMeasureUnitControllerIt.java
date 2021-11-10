package com.airbus.retex.measureUnit;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.mesureUnit.MeasureUnit;
import com.airbus.retex.persistence.measureUnit.MeasureUnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class GetMeasureUnitControllerIt extends BaseControllerTest {
    @Autowired
    private MeasureUnitRepository measureUnitRepository;

    private static final String API_MEASURE_UNITS = "/api/measure-units";
    private MeasureUnit measureUnitOne;
    private MeasureUnit measureUnitTwo;
    private String MEASURE_UNIT_MM_FR = "mm_FR";
    private String MEASURE_UNIT_MM_EN = "mm_EN";
    private String MEASURE_UNIT_MM2_FR = "mm2_FR";
    private String MEASURE_UNIT_MM2_EN = "mm2_EN";

    @BeforeEach
    public void before() {
        measureUnitOne = dataSetInitializer.createMeasureUnit(MEASURE_UNIT_MM_FR, MEASURE_UNIT_MM_EN);
        measureUnitTwo = dataSetInitializer.createMeasureUnit(MEASURE_UNIT_MM2_FR, MEASURE_UNIT_MM2_EN);
    }

    @Test
    public void getAllMeasureUnits_ok() throws Exception {
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_superAdmin;
        withRequest = get(API_MEASURE_UNITS);
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING_COMPONENT, dataset.user_superAdmin, EnumRightLevel.READ);
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_superAdmin, EnumRightLevel.READ);
        abstractCheck()
                .andExpect(jsonPath("$", hasSize((int) measureUnitRepository.count())))
                .andExpect(jsonPath("$[*].id", notNullValue()))
                .andExpect(jsonPath("$[*].translatedFields").isArray())
                .andExpect(jsonPath("$[*].translatedFields.FR.name", notNullValue()))
                .andExpect(jsonPath("$[*].translatedFields.EN.name", notNullValue()));
    }
}
