package com.airbus.retex.business.dto.enumeration;

import lombok.Getter;

@Getter
public enum FunctionalityFields {
    FIELD_NAME("name");

    private String value;

    FunctionalityFields(String val) {
        this.value = val;
    }

    public String getValue() {
        return this.value;
    }
}
