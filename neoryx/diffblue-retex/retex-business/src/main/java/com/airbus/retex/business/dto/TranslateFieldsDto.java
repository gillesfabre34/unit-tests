package com.airbus.retex.business.dto;

import java.util.EnumSet;
import java.util.Set;

public class TranslateFieldsDto implements Dto {

    private TranslatedDto translatedDto;
    private Set<Enum> fields;

    public TranslateFieldsDto(TranslatedDto translatedDto, Class<? extends Enum> fields) {
        this.translatedDto = translatedDto;
        this.fields = EnumSet.allOf(fields);
    }

    public String getClassName() {
        return translatedDto.getClassName();
    }

    public Long getEntityId() {
        return translatedDto.getEntityId();
    }

    public Long getRevision() {
        return translatedDto.getRevision();
    }

    public Set<Enum> getFields() {
        return fields;
    }
}
