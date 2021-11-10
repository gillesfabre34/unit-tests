package com.airbus.retex.business.dto.drt;


import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.damage.DamageNameDto;
import com.airbus.retex.business.dto.step.StepCustomDrtDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DrtInspectionDetailsDto implements Dto {
    //Id sent corresponding of routingComponent id
    private Long idRoutingComponentIndex;

    private DamageNameDto damage;

    //list de posts avec threshold
    private List<StepCustomDrtDto> steps;


}
