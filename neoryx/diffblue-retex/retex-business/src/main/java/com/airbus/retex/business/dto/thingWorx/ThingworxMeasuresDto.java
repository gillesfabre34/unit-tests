package com.airbus.retex.business.dto.thingworx;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.step.StepCustomDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ThingworxMeasuresDto implements Dto {

    private List<StepCustomDto> steps;
}
