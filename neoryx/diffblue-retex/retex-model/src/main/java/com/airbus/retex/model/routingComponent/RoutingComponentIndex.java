package com.airbus.retex.model.routingComponent;

import com.airbus.retex.model.IRoutingComponentModel;
import com.airbus.retex.model.TranslatableModel;
import com.airbus.retex.model.basic.AbstractVersionableModel;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.todoList.TodoList;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
public class RoutingComponentIndex extends AbstractVersionableModel<Long> implements TranslatableModel<RoutingComponentIndexTranslation, RoutingComponentFieldsEnum> {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "routing_component_id", nullable = true)
    private RoutingComponent routingComponent;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_list_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TodoList todoList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "entity")
    private Set<RoutingComponentIndexTranslation> translations = new HashSet<>();

    public void addTranslation(RoutingComponentIndexTranslation translation) {
        translation.setEntity(this);
        translations.add(translation);
    }


    //  TODO : WTF que'est-ce qu'il fo là lui
    @Transient
    private String name; //FIXME improve to manage this on presentation layer


    // --------------------------------
    public OperationType getOperationType() {
        if (null != routingComponent) {
            return routingComponent.getOperationType();
        } else if (null != todoList) {
            return todoList.getOperationType();
        }

        throw new IllegalStateException();
    }

    public IRoutingComponentModel getRelatedModel() {
        if (null != routingComponent) {
            return routingComponent;
        } else if (null != todoList) {
            return todoList;
        }

        throw new IllegalStateException();
    }
}
