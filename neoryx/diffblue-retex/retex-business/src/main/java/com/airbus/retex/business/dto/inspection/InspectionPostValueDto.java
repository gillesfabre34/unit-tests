package com.airbus.retex.business.dto.inspection;

import com.airbus.retex.business.converter.FloatConverter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InspectionPostValueDto {

    private Long postThresholdId;

    @JsonDeserialize(converter = FloatConverter.class)
    private Object value; //Float RC or Boolean for Visual

}
