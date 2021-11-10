package com.airbus.retex.thingworx;

import com.airbus.retex.business.dto.step.StepThingworxDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.config.RetexConfig;
import com.airbus.retex.model.user.User;
import com.airbus.retex.service.impl.thingworx.ThingWorxServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ThingWorxServiceTU {
    private static final String THING_WORX_ERROR_API_KEY = "Error : Thingworx Api key is empty";

    @Mock
    private Authentication authentication;
    @Mock
    private MessageSource messageSource;
    @InjectMocks
    private ThingWorxServiceImpl thingWorxService;
    @Mock
    private RetexConfig retexConfig;

    private User withUser;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);

        withUser = new User();
        withUser.setId(1L);
    }

    @Test
    public void postConstruct_test_thingworx_api_key_error() throws Exception {
        when(retexConfig.getThingworxApikey()).thenReturn("");
        checkConfigValues(THING_WORX_ERROR_API_KEY);
    }

    private void checkConfigValues(String expectedMessage) {
        Exception thrown = assertThrows(Exception.class, () -> {
            thingWorxService.postConstruct();
        });
        assertEquals(thrown.getMessage(), expectedMessage);
    }

    @Test
    public void postMeasures_empty_context() throws Exception {
        StepThingworxDto thingworxDto = createThingworxDto();
        testServicePostMeasures("retex.error.thingworx.context.not.found", thingworxDto);
    }

    /* -------------------------------------------------
     * --------- factorisation functions ---------------
     * ------------------------------------------------- */

    private void testServiceGetMainUrlCall(String errorCode) throws IOException {
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            thingWorxService.getThingworxMainUrl(withUser, 1L, 1L, 1L, 1L, 1L, Locale.FRENCH);
        });
        checkException(thrown, errorCode);
    }

    private void testServicePostMeasures(String errorCode, StepThingworxDto thingworxData) throws IOException {
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            thingWorxService.postMeasures(thingworxData);
        });
        checkException(thrown, errorCode);
    }

    private void checkException(Exception e, String errorCode) {
        assertEquals(messageSource.getMessage(e.getMessage(), null, null),
                messageSource.getMessage(errorCode, null, null));
    }

    public StepThingworxDto createThingworxDto() {
        StepThingworxDto thingworxDto = new StepThingworxDto();
        thingworxDto.setContext("");
        thingworxDto.setSteps(new ArrayList<>());
        return thingworxDto;
    }
}
