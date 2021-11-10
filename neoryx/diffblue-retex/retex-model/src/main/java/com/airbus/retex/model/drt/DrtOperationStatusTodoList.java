package com.airbus.retex.model.drt;

import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.todoList.TodoList;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@DiscriminatorValue("DRT_OTL")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrtOperationStatusTodoList extends AbstractDrtOperationStatus {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id")
    private Operation operation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_list_id")
    private TodoList todoList;

}
