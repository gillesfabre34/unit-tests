package com.airbus.retex.part;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.part.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class SearchPartControllerIT extends BaseControllerTest {

    private Part partOne;
    private Part partTwo;

    private FunctionalArea functionalAreaOne;
    private FunctionalArea functionalAreaTwo;

    private final static String API_PART_NUMBERS = "/api/parts/part-numbers";


    @BeforeEach
    public void before() {
        dataset.functionalArea_1 = null;
        dataset.functionalArea_2 = null;
        partOne = dataSetInitializer.createPart(
                part -> {
                    part.setAta(dataset.ata_1);
                    part.setPartDesignation(dataset.partDesignation_planetGear);
                    part.setPartNumber("55555555");
                    part.setPartNumberRoot("AH12");
                    part.setStatus(EnumStatus.VALIDATED);

                }, null
        );

        partTwo = dataSetInitializer.createPart(
                part -> {
                    part.setAta(dataset.ata_1);
                    part.setPartDesignation(dataset.partDesignation_lowerLeg);
                    part.setPartNumber("55555554");
                    part.setPartNumberRoot("AH12");
                    part.setStatus(EnumStatus.VALIDATED);
                }, null
        );

        functionalAreaOne = dataSetInitializer.createFunctionalArea(functionalArea -> {
            functionalArea.setPart(partOne);
        });

        dataset.part_example = partOne;


        dataSetInitializer.createUserFeature(FeatureCode.PART_MAPPING, dataset.user_simpleUser, EnumRightLevel.WRITE);
    }

    @Test
    public void searchAllPartsByPN_OK_filteredByFA() throws Exception {
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.OK;

        searchAllParts("5555").andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].partNumber", containsString("55555555")));
    }


    @Test
    public void searchAllPartsByPN_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.OK;

        functionalAreaTwo = dataSetInitializer.createFunctionalArea(functionalArea -> {
            functionalArea.setPart(partTwo);
        });

        searchAllParts("5555").andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].partNumber", containsString("55555554")))
                .andExpect(jsonPath("$[1].partNumber", containsString("55555555")));
    }

    private ResultActions searchAllParts(String search) throws Exception {
        withRequest = get(API_PART_NUMBERS).param("search", search);

        return abstractCheck();
    }

}
