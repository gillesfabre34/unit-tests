package com.airbus.retex.business.util.annotation;


import com.airbus.retex.business.util.validator.DamageNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Constraint(validatedBy = { DamageNameValidator.class })
@Target({ FIELD, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NameValidation {
    String message() default "Invalid value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}