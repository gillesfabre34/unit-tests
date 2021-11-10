package com.airbus.retex.business.dto.routingComponent;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.step.StepCreationDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoutingComponentCreateUpdateDto implements Dto {

    @Nullable
    private Long operationTypeId;
    @Nullable
    private String inspectionValue;
    @Nullable
    private Long taskId;
    @Nullable
    private Long subTaskId;
    @Nullable
    private EnumStatus status;

    private List<StepCreationDto> steps;
}