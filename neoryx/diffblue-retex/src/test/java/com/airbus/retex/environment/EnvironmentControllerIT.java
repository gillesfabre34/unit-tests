package com.airbus.retex.environment;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.persistence.environment.EnvironmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class EnvironmentControllerIT extends BaseControllerTest {

    private static final String API_ENVIRONMENTS = "/api/environments";

    @Autowired
    private EnvironmentRepository environmentRepository;

    /**
     * Get all Environments
     */
    @Test
    void getAllEnvironments_ok() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_ENVIRONMENTS);
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$", hasSize((int) environmentRepository.count())))
                .andExpect(jsonPath("$[0].id", isA(Integer.class)))
                .andExpect(jsonPath("$[0].name",startsWith("Environment number")));
    }

    @Test
    void getAllEnvironments_forbidden() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_ENVIRONMENTS);
        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }
}
