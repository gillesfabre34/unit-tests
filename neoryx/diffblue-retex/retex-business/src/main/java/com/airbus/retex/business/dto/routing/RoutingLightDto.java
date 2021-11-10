package com.airbus.retex.business.dto.routing;

import com.airbus.retex.business.dto.ITranslationDto;
import com.airbus.retex.business.dto.IVersionableOutDto;
import com.airbus.retex.business.dto.TranslationDto;
import com.airbus.retex.model.routing.RoutingFieldsEnum;
import com.airbus.retex.model.routing.RoutingTranslation;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoutingLightDto implements IVersionableOutDto, ITranslationDto<RoutingTranslation, RoutingFieldsEnum> {

    @JsonIgnore
    private Long technicalId;

    @JsonIgnore
    private Long naturalId;

    private String code;

    private TranslationDto name = new TranslationDto(this, RoutingFieldsEnum.name);

    @Override
    public Class<RoutingTranslation> getClassTranslation() {
        return RoutingTranslation.class;
    }

    @Override
    public Long getEntityId() {
        return this.technicalId;
    }

    @Override
    public Long getId() {
        return this.naturalId;
    }
}
