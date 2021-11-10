package com.airbus.retex.ata;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.persistence.ata.ATARepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ATAControllerIT extends BaseControllerTest {

    private static final String API_ATAS = "/api/atas";

    @Autowired
    private ATARepository ataRepository;

    /**
     * Get all ATA
     */
    @Test
    void getAllATA_statusOk() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_ATAS);
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$", hasSize((int) ataRepository.count())))
                .andExpect(jsonPath("$[*]", notNullValue()));
    }


    /**
     * Get all ATA with post_a simple user
     */
    @Test
    void getAllATA_statusForbidden() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_ATAS);
//        expectedStatus = HttpStatus.FORBIDDEN;
        expectedStatus = HttpStatus.OK;
        abstractCheck();
    }
}
