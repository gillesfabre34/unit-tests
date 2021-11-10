package com.airbus.retex.business.dto.routingComponent;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.inspection.InspectionDto;
import com.airbus.retex.business.dto.operationType.OperationTypeDto;
import com.airbus.retex.business.dto.subTask.SubTaskDamageDto;
import com.airbus.retex.business.dto.task.TaskDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoutingComponentFilterDto implements Dto {

    private List<OperationTypeDto> operationTypeList;

    private List<TaskDto> todoListList;

    private List<TaskDto> functionalityList;

    private List<SubTaskDamageDto> subTaskList;

    private List<InspectionDto> inspectionList;

    private List<EnumStatus> statusList;
}
