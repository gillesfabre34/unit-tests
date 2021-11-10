package com.airbus.retex.business.dto.routingComponent;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.damage.DamageNameDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoutingComponentIndexNameDto implements Dto {

    private DamageNameDto damage;
    private Long idRoutingComponentIndex;
}

