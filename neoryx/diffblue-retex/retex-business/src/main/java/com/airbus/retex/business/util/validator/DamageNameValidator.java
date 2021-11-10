package com.airbus.retex.business.util.validator;

import com.airbus.retex.business.util.ConstantRegex;
import com.airbus.retex.business.util.annotation.NameValidation;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.damage.DamageFieldsEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.regex.Pattern;

public class DamageNameValidator implements ConstraintValidator<NameValidation, Map<Language, Map<DamageFieldsEnum, String>>> {

    @Override
    public boolean isValid(Map<Language, Map<DamageFieldsEnum, String>> value, ConstraintValidatorContext context) {
        boolean isValid = true;

        Language[] languages = Language.values();
        if(null == value) isValid = false;
        int i = 0;
        while (i<languages.length && isValid){
            Language language = languages[i];
            if(value.get(language) != null && !Pattern.matches(ConstantRegex.REGEX_ALPHA_ACCENT_DASH_SPACE, value.get(language).get(DamageFieldsEnum.name))){
                isValid = false;
            }
            i++;
        }

        return isValid;
    }

}
