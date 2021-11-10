package com.airbus.retex.business.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

public abstract class TranslatedDto implements Dto {

    @Getter
    @Setter
    private Long revision;

    @JsonIgnore
    public abstract String getClassName();

    @JsonIgnore
    public abstract Long getEntityId();
}
