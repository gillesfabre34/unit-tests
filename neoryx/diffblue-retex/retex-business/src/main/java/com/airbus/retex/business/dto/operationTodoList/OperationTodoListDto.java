package com.airbus.retex.business.dto.operationTodoList;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.todoList.TodoListDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperationTodoListDto implements Dto {

    private TodoListDto todoList;

    private EnumStatus operationStatus;

}
