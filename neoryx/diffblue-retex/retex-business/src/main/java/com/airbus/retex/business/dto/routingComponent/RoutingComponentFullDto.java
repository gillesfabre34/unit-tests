package com.airbus.retex.business.dto.routingComponent;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.VersionableOutDto;
import com.airbus.retex.business.dto.operationType.OperationTypeDto;
import com.airbus.retex.business.dto.step.StepFullDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RoutingComponentFullDto extends VersionableOutDto implements Dto {
    private Long routingComponentIndexId;
    private OperationTypeDto operationType;
    private Long taskId;
    private Long subTaskId;
    private String inspectionValue;

    private Boolean isLatestVersion;
    private Long versionNumber;
    private EnumStatus status;
    private LocalDateTime creationDate;

    private List<StepFullDto> steps;
}
