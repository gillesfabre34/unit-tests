package com.airbus.retex.business.dto.inspection;


import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.damage.DamageNameDto;
import com.airbus.retex.business.dto.step.StepCustomDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoutingInspectionDetailsDto implements Dto {
    //Id sent corresponding of routingComponent id
    private Long idRoutingComponentIndex;

    private DamageNameDto damage;

    //list de posts avec threshold
    private List<StepCustomDto> steps;

    private Boolean isLatestVersion;
    private EnumStatus status;

}
