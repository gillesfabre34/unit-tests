package com.airbus.retex.business.converter;

import com.fasterxml.jackson.databind.util.StdConverter;

public class FloatConverter extends StdConverter<Object, Object> {

    @Override
    public Object convert(Object value) {
        if(value instanceof Double){
            return ((Double) value).floatValue();
        }
        if(value instanceof Integer){
            return ((Integer) value).floatValue();
        } else return value;
    }
}
