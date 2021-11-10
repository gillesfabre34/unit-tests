package com.airbus.retex.business.annotation;

import java.util.Map;

import javax.validation.ConstraintValidator;

import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.routing.RoutingFieldsEnum;

public class RoutingTranslatedFieldsValidator extends AbstractTranslatedFieldsValidator<RoutingFieldsEnum>
        implements ConstraintValidator<RequireTranslation, Map<Language, Map<RoutingFieldsEnum, String>>> {

}
