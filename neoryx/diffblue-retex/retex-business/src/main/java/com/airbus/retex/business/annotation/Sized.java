package com.airbus.retex.business.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = {SizedValidator.class})
public @interface Sized {

    String message() default "{Sized dimensions not set properly}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] dimensions() default {"width", "height"};
}
