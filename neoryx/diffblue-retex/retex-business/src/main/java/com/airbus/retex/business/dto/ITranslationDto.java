package com.airbus.retex.business.dto;

import com.airbus.retex.model.AbstractTranslation;
import com.fasterxml.jackson.annotation.JsonIgnore;

public interface ITranslationDto<T extends AbstractTranslation<E>, E extends Enum> {

    @JsonIgnore
    Class<T> getClassTranslation();

    @JsonIgnore
    Long getEntityId();
}
