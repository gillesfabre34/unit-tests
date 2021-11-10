package com.airbus.retex.request;

import com.airbus.retex.business.dto.request.RequestCreationDto;
import com.airbus.retex.helper.DtoHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequestCreationDtoValidationUT {


    private ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private Validator validator = validatorFactory.getValidator();
    private RequestCreationDto dto;

    @BeforeEach
    public void setup(){
        dto = DtoHelper.generateValidCreationDto();
        dto.setAirbusEntityId(1L);
    }

    @Test void dueDteMustNotBeAPastDate(){
        // Future
        assertTrue(validator.validate(dto).size() == 0);
        // Past
        dto.setDueDate(LocalDate.now().minusDays(1));
        assertTrue(validator.validate(dto).size() == 1);
        // Today
        dto.setDueDate(LocalDate.now());
        assertTrue(validator.validate(dto).size() == 0);
    }
}
