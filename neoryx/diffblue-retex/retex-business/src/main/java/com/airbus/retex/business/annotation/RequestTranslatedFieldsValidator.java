package com.airbus.retex.business.annotation;

import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.request.RequestFieldsEnum;

import javax.validation.ConstraintValidator;
import java.util.Map;

public class RequestTranslatedFieldsValidator extends AbstractTranslatedFieldsValidator<RequestFieldsEnum>
        implements ConstraintValidator<RequireTranslation, Map<Language, Map<RequestFieldsEnum, String>>> {

}
