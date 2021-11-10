package com.airbus.retex.service.todoList;

import com.airbus.retex.business.dto.todoList.TodoListDto;
import com.airbus.retex.model.common.Language;

import java.util.List;

public interface ITodoListService {

    List<TodoListDto> findTodoListByOperationTypesId(Long operationTypeId, Language language);
}
