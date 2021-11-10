package com.airbus.retex.part;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.dataset.DatasetInitializer;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.part.PartDesignation;
import com.airbus.retex.model.part.PartDesignationFieldsEnum;
import com.airbus.retex.persistence.part.PartDesignationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class PartDesignationControllerIT extends BaseControllerTest {

    private static final String API_DESIGNATIONS = "/api/designations";
    private static final String FR_TEXT = "Texte en français";
    private static final String EN_TEXT = "Text in english";

    @Autowired
    private PartDesignationRepository partDesignationRepository;

    @Autowired
    private DatasetInitializer datasetInitializer;

    private PartDesignation partDesignation;

    @BeforeEach
    public void before() {
        partDesignation = new PartDesignation();
        partDesignation = partDesignationRepository.save(partDesignation);
        datasetInitializer.createTranslate(partDesignation, PartDesignationFieldsEnum.designation, Language.EN , EN_TEXT );
        datasetInitializer.createTranslate(partDesignation, PartDesignationFieldsEnum.designation, Language.FR , FR_TEXT);
    }


    /**
     * Get all part designation
     */
    @Test
    void getAllPartDesignation_statusOk() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_DESIGNATIONS);
        expectedStatus = HttpStatus.OK;;

        abstractCheck()
                .andExpect(jsonPath("$", hasSize((int) partDesignationRepository.count())))
                .andExpect(jsonPath("$[*].id", hasItem(equalTo(partDesignation.getId().intValue()))))
                .andExpect(jsonPath("$[*].designation", hasItem(EN_TEXT)));

    }
}
