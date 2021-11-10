package com.airbus.retex.business.dto.routingComponent;

import com.airbus.retex.business.dto.common.PaginationDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoutingComponentFilteringDto extends PaginationDto {

    private Long operationTypeId;
    private Long todoListNameId;
    private Long functionalityId;
    private Long subTaskId;
    private Long inspectionId;
    private EnumStatus status;
}
