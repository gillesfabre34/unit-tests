package com.airbus.retex.model.task;

import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.model.translation.Translate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
public class TodoListName extends AbstractBaseModel {
    public static final String FIELD_NAME = "name";

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "todoListNameId")
    private Set<TodoList> todoLists;

    @Column(name = "operation_type_id")
    private Long operationTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_type_id", insertable = false, updatable = false)
    public OperationType operationType;

    @OneToMany(mappedBy = "entityId", fetch = FetchType.LAZY)
    @Where(clause = "class_name = 'TodoListName'")
    private List<Translate> translates  = new ArrayList<>();
}
