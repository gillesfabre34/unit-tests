package com.airbus.retex.business.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = {RequestTranslatedFieldsValidator.class, RoutingTranslatedFieldsValidator.class})
public @interface RequireTranslation {

    String message() default "{ValidatedTranslation}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] fields() default "";

    boolean all() default true;
}
