package com.airbus.retex.business.dto.inspection;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class InspectionQCheckValueDto {

    @NotNull
    private Long idRoutingComponentIndex;

    private Boolean qCheckValue;

}
