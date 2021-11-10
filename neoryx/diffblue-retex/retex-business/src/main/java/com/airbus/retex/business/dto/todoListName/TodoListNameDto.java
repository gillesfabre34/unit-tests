package com.airbus.retex.business.dto.todoListName;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.business.dto.TranslatedDto;
import com.airbus.retex.model.task.TodoListName;
import com.airbus.retex.model.todoList.TodoListFieldsEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoListNameDto extends TranslatedDto {
    private Long id;

    private final TranslateDto name = new TranslateDto(this, TodoListFieldsEnum.name);

    @Override
    public String getClassName() {
        return TodoListName.class.getSimpleName();
    }

    @Override
    public Long getEntityId() {
        return id;
    }
}

