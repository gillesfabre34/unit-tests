package com.airbus.retex.business.dto.routingComponent;

import com.airbus.retex.business.dto.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoutingComponentFilterRequestDto implements Dto {

    private Long operationTypeId;
    private Long functionalityId;
    private Long todoListNameId;
    private Long subTaskId;


}
