package com.airbus.retex.business.dto.operation;

import com.airbus.retex.business.dto.operationTodoList.OperationTodoListDto;
import com.airbus.retex.business.dto.operationType.OperationTypeDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class OperationFullDto extends OperationDto {

    private OperationTypeDto operationType;

    @Nullable
    private List<OperationTodoListDto> operationTodoLists;

    @Nullable
    private List<OperationFunctionalAreaStatusDto> functionalAreaStatus;
}
