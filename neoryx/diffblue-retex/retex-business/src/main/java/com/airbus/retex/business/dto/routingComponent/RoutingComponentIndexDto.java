package com.airbus.retex.business.dto.routingComponent;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.VersionableOutDto;
import com.airbus.retex.business.dto.operationType.OperationTypeDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RoutingComponentIndexDto extends VersionableOutDto implements Dto {

    private String name;

    private LocalDateTime creationDate;

    private OperationTypeDto operationType;

    private EnumStatus status;

    private boolean deletable;

}
