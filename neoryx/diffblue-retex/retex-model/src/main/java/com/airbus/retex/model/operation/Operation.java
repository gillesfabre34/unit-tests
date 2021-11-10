package com.airbus.retex.model.operation;

import com.airbus.retex.model.basic.AbstractVersionableChildModel;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.todoList.TodoList;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Entity
@Getter
@Setter
@Audited
@AllArgsConstructor
@NoArgsConstructor
public class Operation extends AbstractVersionableChildModel<Long> {

    @Column(name = "order_number")
    private Integer orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routing_id")
    private Routing routing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_type_id")
    private OperationType operationType;

    @Setter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "operation", targetEntity = OperationFunctionalArea.class)
    private Set<OperationFunctionalArea> operationFunctionalAreas = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "operation_todo_list",
        joinColumns = { @JoinColumn(name = "operation_id") },
        inverseJoinColumns = { @JoinColumn(name = "todo_list_id") }
    )
    @OrderBy("naturalId")
    private SortedSet<TodoList> todoLists = new TreeSet<>();

    public void addTodoList(TodoList todoList) {
        this.todoLists.add(todoList);
    }

    public void removeTodoList(TodoList todoList) {
        this.todoLists.remove(todoList);
    }

    public void addOperationFunctionalArea(OperationFunctionalArea operationFunctionalArea) {
        operationFunctionalArea.setOperation(this);
        this.operationFunctionalAreas.add(operationFunctionalArea);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return this.getTechnicalId() != null
                && this.getTechnicalId().equals(operation.getTechnicalId())
                && orderNumber.equals(operation.orderNumber)
                && routing.getTechnicalId().equals(operation.getRouting().getTechnicalId());
    }
    @Override
    public int hashCode() {
        int result = 17;
        int idHash = technicalId == null ? 0 : technicalId.hashCode();
        int routingIdHash = 0;
        if (routing != null) {
            routingIdHash = routing.getNaturalId() == null ? 0 : routing.getNaturalId().hashCode();
        }
        result = 31 * result + idHash;
        result = 31 * result + orderNumber.hashCode();
        result = 31 * result + routingIdHash;
        return result;
    }

}
