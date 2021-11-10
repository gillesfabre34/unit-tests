package com.airbus.retex.business.dto.routingComponent;

import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.routingComponent.RoutingComponentFieldsEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RoutingComponentDto {
    public static final String FIELD_INFORMATION = "information";
    public static final String FIELD_NAME = "name";

    private Map<Language, Map<RoutingComponentFieldsEnum, String>> translatedFields;

    private List<Object> componentNameParts;

    private EnumStatus status;

    private LocalDateTime creationDate;
}
