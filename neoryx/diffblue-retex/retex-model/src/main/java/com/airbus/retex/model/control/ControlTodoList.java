package com.airbus.retex.model.control;

import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.todoList.TodoList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Getter
@Setter
@DiscriminatorValue("TodoList")
@NoArgsConstructor
@AllArgsConstructor
public class ControlTodoList extends AbstractControl<EnumTodoListValue> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_list_id")
    private TodoList todoList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id")
    private Operation operation;

    @Column(name = "todo_list_value")
    private EnumTodoListValue value;
}
