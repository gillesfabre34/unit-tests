package com.airbus.retex.business.annotation;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotReadablePropertyException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class SizedValidator implements ConstraintValidator<Sized, Object> {
    public static final String MISSING_PROPERTIES_MESSAGE = " are required and not set";
    private String[] dimensions;
    private boolean missingProperty = false;
    private StringJoiner sj = new StringJoiner(", ");

    @Override
    public void initialize(Sized constraint) {
        dimensions = constraint.dimensions();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Integer countHavingValue = List.of(dimensions).stream()
                .map(
                        item -> {
                            try {
                                // prepare for sum
                                Object val = new BeanWrapperImpl(value).getPropertyValue(item);
                                if(val != null){
                                    return 1;
                                }
                                return null;
                            } catch (NotReadablePropertyException e) {
                                this.missProperty(item);
                                return null;
                            }
                        }
                        // prevent sum with null value
                ).filter(Objects::nonNull).reduce(
                        0, Integer::sum
                );
        if((countHavingValue.equals(dimensions.length) && ! missingProperty) || (countHavingValue.equals(0) && ! missingProperty)){
            // Ok when every dimension is set, or when none (for now not required)
            return true;
        }
        // annotated object is not defined properly

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(sj.toString() + MISSING_PROPERTIES_MESSAGE).addConstraintViolation();
        return false;
    }

    private void missProperty(String item){
        sj.add(item);
        this.missingProperty = true;
    }
}
