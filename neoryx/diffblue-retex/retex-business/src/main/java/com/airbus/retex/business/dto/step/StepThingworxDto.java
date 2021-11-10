package com.airbus.retex.business.dto.step;

import com.airbus.retex.business.dto.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StepThingworxDto implements Dto {
    // context could be ldap user sent from thingworx
    private String context;
    private List<StepCustomThingworxDto> steps;
}
