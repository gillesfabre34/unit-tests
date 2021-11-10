package com.airbus.retex.business.annotation;

import com.airbus.retex.business.dto.media.ThumbnailSizeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SizedUT {

    private ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private Validator validator = validatorFactory.getValidator();
    private ThumbnailSizeDto dto;

    @BeforeEach
    public void setup(){
        dto = new ThumbnailSizeDto();
        dto.setWidth(6);
        dto.setHeight(8);
    }

    @Test
    public void validation_valid_class_with_values(){
        List.of(
                dto,
                new DefaultWellSizedDto(1, 2),
                new SpecificWellSizedDto(1, 2, 3)
        ).forEach(
                item ->  assertTrue(validator.validate(item).isEmpty())
        );
    }

    @Test
    public void validation_valid_class_with_no_value(){
        dto.setWidth(null);
        dto.setHeight(null);
        List.of(
                dto,
                new DefaultWellSizedDto(null, null),
                new SpecificWellSizedDto(null, null, null)
        ).forEach(
                item ->  assertTrue(validator.validate(item).isEmpty())
        );
    }

    @Test
    public void validation_valid_class_with_missing_values(){
        dto.setWidth(null);
        List.of(
                dto,
                new DefaultWellSizedDto(1, null),
                new SpecificWellSizedDto(1, null, 3)
        ).forEach(
                item ->  assertTrue(validator.validate(item).size() > 0)
        );
    }

    @Test
    public void validation_wrong_class_with_values(){
        List.of(
                new MisspecifiedSizedDto(1, 2)
        ).forEach(
                item ->  {
                    Set<ConstraintViolation<Object>> result = validator.validate(item);
                    assertTrue( result.size() > 0);
                    result.forEach(
                            it -> assertEquals(it.getMessage(), "x, y" + SizedValidator.MISSING_PROPERTIES_MESSAGE)
                    );
                }
        );
    }

    @Data
    @Sized()
    @AllArgsConstructor
    private class DefaultWellSizedDto{
        private Integer width;
        private Integer height;
    }

    @Data
    @Sized(dimensions = {"x", "y", "z"})
    @AllArgsConstructor
    private class SpecificWellSizedDto{
        private Integer x;
        private Integer y;
        private Integer z;
    }

    @Data
    @Sized(dimensions = {"x", "y"})
    @AllArgsConstructor
    private class MisspecifiedSizedDto{
        private Integer width;
        private Integer height;
    }
}
