package com.airbus.retex.origin;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.utils.ConstantUrl;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class OriginControllerIT extends BaseControllerTest {

    @Test
    public void getAllOrigins_Ok() throws Exception{
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.OK;
        check(ConstantUrl.API_ORIGINS).andExpect(jsonPath("$", hasSize(3)));
    }

    private ResultActions check(String endpoint) throws Exception {
        withRequest = get(endpoint);

        return abstractCheck();
    }
}
