package com.airbus.retex.business.annotation;

import com.airbus.retex.business.dto.request.RequestCreationDto;
import com.airbus.retex.helper.DtoHelper;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.request.RequestFieldsEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequireTranslationUT {

    private ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private Validator validator = validatorFactory.getValidator();
    private RequestCreationDto dto;

    @BeforeEach
    public void setup(){
        dto = DtoHelper.generateValidCreationDto();
        dto.setAirbusEntityId(1L);
    }
    @Test
    public void validation_empty_translation(){
        dto.getTranslatedFields().get(Language.FR).put(RequestFieldsEnum.name, "");
        Set<ConstraintViolation<RequestCreationDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("ValidatedTranslation"));

    }
    @Test
    public void validation_missing_language(){
        dto.getTranslatedFields().remove(Language.FR);
        Set<ConstraintViolation<RequestCreationDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("ValidatedTranslation"));
    }
}
