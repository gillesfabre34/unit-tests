package com.airbus.retex.exception;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.ata.ATA;
import com.airbus.retex.persistence.ata.ATARepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ControllerExceptionHandlerIT extends BaseControllerTest {

    private static final String ERROR_500_PATTERN = "An error occurred : code NestedServletException@[0-9]+";

    @Autowired
    private ATARepository ataRepository;

    private String expectedMessagePattern = null;

    @Test
    public void testThrowError () throws Exception {
        withRequest = get("/test/throw-error");
        expectedStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        expectedMessagePattern = ERROR_500_PATTERN;

        check();
    }

    @Test
    public void testThrowFunctionalException () throws Exception {
        withRequest = get("/test/throw-functional");
        expectedStatus = HttpStatus.BAD_REQUEST;
        expectedMessagePattern = "the labels chosen for this role already exists";

        check();
    }

    @Test
    public void testThrowNotFound () throws Exception {
        withRequest = get("/test/throw-not-found");
        expectedStatus = HttpStatus.NOT_FOUND;
        expectedMessagePattern = "User not found";

        check();
    }

    @Test
    public void testThrowErrorRollback () throws Exception {
        withRequest = get("/test/save-and-throw-error");
        expectedStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        expectedMessagePattern = ERROR_500_PATTERN;

        check();

        Optional<ATA> ataOpt = ataRepository.findByCode(ExceptionTestController.TEST_ATA_CODE);
        assertThat(ataOpt.isPresent(), is(false));
    }

    @Test
    public void testThrowFunctionalRollback () throws Exception {
        withRequest = get("/test/save-and-throw-functional");
        expectedStatus = HttpStatus.BAD_REQUEST;
        expectedMessagePattern = ".*";

        check();

        Optional<ATA> ataOpt = ataRepository.findByCode(ExceptionTestController.TEST_ATA_CODE);
        assertThat(ataOpt.isPresent(), is(false));
    }

    @Test
    public void testSave () throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get("/test/save");
        expectedStatus = HttpStatus.OK;
        checkResponseContentType = false;

        abstractCheck();

        Optional<ATA> ataOpt = ataRepository.findByCode(ExceptionTestController.TEST_ATA_CODE);
        assertThat(ataOpt.isPresent(), is(true));
    }

    private void check() throws Exception {
        ResultActions resultActions = abstractCheck();
        if(expectedMessagePattern != null) {
            resultActions.andExpect(jsonPath("$.messages", hasItem(matchesPattern(expectedMessagePattern))));
        }
    }
}
