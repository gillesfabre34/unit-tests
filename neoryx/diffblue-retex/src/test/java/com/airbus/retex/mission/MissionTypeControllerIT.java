package com.airbus.retex.mission;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.persistence.mission.MissionTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class MissionTypeControllerIT extends BaseControllerTest {


    private static final String API_MISSIONS_TYPE = "/api/missions";

    @Autowired
    private MissionTypeRepository missionTypeRepository;

    /**
     * Get all Mission Type
     */
    @Test
    void getAllMissions_ok() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_MISSIONS_TYPE);
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$", hasSize((int) missionTypeRepository.count())))
                .andExpect(jsonPath("$[*]", notNullValue()))
                .andExpect(jsonPath("$[0].id", isA(Integer.class)))
                .andExpect(jsonPath("$[0].name",startsWith("Mission type number")));

    }

    @Test
    void getAllMissions_forbidden() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_MISSIONS_TYPE);
        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }
}

