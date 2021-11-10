package com.airbus.retex.business.dto.routing;

import com.airbus.retex.business.annotation.RequireTranslation;
import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.routing.RoutingFieldsEnum;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Setter
public class RoutingCreationDto implements Dto {

    @NotNull
    @RequireTranslation(all = true)
    private Map<Language, Map<RoutingFieldsEnum, String>> translatedFields;

    private Long partId;

    private String partNumberRoot;

}
