package com.airbus.retex.model.todoList;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.envers.Audited;

import com.airbus.retex.model.IRoutingComponentModel;
import com.airbus.retex.model.basic.AbstractVersionableChildModel;
import com.airbus.retex.model.inspection.Inspection;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.task.TodoListName;

import lombok.Getter;
import lombok.Setter;

@Entity
@Audited
@Getter
@Setter
public class TodoList extends AbstractVersionableChildModel<Long> implements IRoutingComponentModel, Comparable <TodoList> {
    public static final String FIELD_INFORMATIONS = "information";
    public static final String FIELD_NAME = "name";

    @Column(name = "todo_list_name_id")
    private Long todoListNameId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_list_name_id", insertable = false, updatable = false)
    private TodoListName todoListName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_type_id")
    private OperationType operationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id")
    private Inspection inspection;

    // RoutingComponent contains many Steps
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "todoList")
    private List<Step> steps = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "todoList")
    private RoutingComponentIndex routingComponentIndex;

    public void setSteps(List<Step> steps){
        this.steps = steps;
        steps.forEach(step -> step.setTodoList(this));
    }

    public void addStep(Step step){
        step.setTodoList(this);
        this.steps.add(step);
    }

    @Override
    public int compareTo(TodoList o) {
        return this.getNaturalId().compareTo(o.getNaturalId());
    }
}
