package com.airbus.retex.airbusEntity;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.utils.ConstantUrl;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collection;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class AirbusEntityControllerIT extends BaseControllerTest {

    @Test
    public void searchAirbusEntities_OK() throws Exception {
        asUser = dataset.user_simpleUser;

        expectedStatus = HttpStatus.OK;

        ResultActions result = check(hasSize(3));
        System.out.println(result.andReturn().getResponse().getContentAsString());
    }

    private ResultActions check(Matcher<? extends Collection> resultsMatcher) throws Exception {
        withRequest = get(ConstantUrl.API_AIRBUS_ENTITIES);

        return abstractCheck()
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", resultsMatcher));
    }
}
