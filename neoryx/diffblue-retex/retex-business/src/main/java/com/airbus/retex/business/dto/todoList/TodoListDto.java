package com.airbus.retex.business.dto.todoList;

import com.airbus.retex.business.dto.VersionableOutDto;
import com.airbus.retex.business.dto.todoListName.TodoListNameDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoListDto extends VersionableOutDto {

    private TodoListNameDto todoListName;

}
