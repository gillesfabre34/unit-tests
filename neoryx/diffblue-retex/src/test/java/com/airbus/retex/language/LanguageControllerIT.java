package com.airbus.retex.language;

import com.airbus.retex.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class LanguageControllerIT extends BaseControllerTest {
    private static final String API_LANGUAGE = "/api/languages";

    @Test
    public void getAllLanguages_statusOk() throws Exception {
        withRequest = get(API_LANGUAGE);
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_superAdmin;

        abstractCheck();
    }
}
