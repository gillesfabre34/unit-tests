package com.airbus.retex.business.annotation;

import java.util.List;
import java.util.Map;

import javax.validation.ConstraintValidatorContext;

import com.airbus.retex.model.common.Language;
import com.airbus.retex.service.impl.translate.TranslationValidator;

public abstract class AbstractTranslatedFieldsValidator<T extends Enum>  {

    private List<String> requiredFields;

    public void initialize(RequireTranslation constraintAnnotation) {
        if(constraintAnnotation.all()) {
            this.requiredFields = null;
        } else {
            this.requiredFields = List.of(constraintAnnotation.fields());
        }
    }

    public boolean isValid(Map<Language, Map<T, String>> value, ConstraintValidatorContext context) {
        return TranslationValidator.validateTranslations(value, this.requiredFields);
    }
}
