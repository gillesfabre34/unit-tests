package com.airbus.retex.business.dto.inspection;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.lang.Nullable;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.damage.DamageNameDto;
import com.airbus.retex.business.dto.step.StepCustomDrtDto;
import com.airbus.retex.business.dto.todoListName.TodoListNameDto;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.control.EnumTodoListValue;
import com.airbus.retex.model.operation.OperationTypeBehaviorEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InspectionDetailsDto implements Dto {
    @Nullable
    private EnumTodoListValue todoListValue; //For TODOLIST

    @Nullable
    private TodoListNameDto todoListName; //For TODOLIST

    @NotNull
    private OperationTypeBehaviorEnum behavior;

    //Id sent corresponding of routingComponent id
    private Long idRoutingComponentIndex;

    private Long version;

    private DamageNameDto damage;

    @Nullable
    private Boolean qcheckValue; //For routingComponent

    //list de posts avec threshold
    private List<StepCustomDrtDto> steps;

    private EnumStatus statusOperation;

}

