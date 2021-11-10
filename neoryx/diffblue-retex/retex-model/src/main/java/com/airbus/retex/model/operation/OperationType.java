package com.airbus.retex.model.operation;

import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.translation.Translate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationType extends AbstractBaseModel {
    public static final String FIELD_NAME = "name";

    @Column(name = "template")
    private String template;

    @Column(name = "behavior")
    @Enumerated(EnumType.STRING)
    private OperationTypeBehaviorEnum behavior;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "operationType")
    private Set<RoutingComponent> routingComponents;

    @OneToMany(mappedBy = "entityId")
    @Where(clause = "class_name = 'OperationType'")
    private List<Translate> translates;

    public boolean isBehavior(OperationTypeBehaviorEnum behavior) {
        return this.behavior.equals(behavior);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationType that = (OperationType) o;
        return Objects.equals(template, that.template) &&
                behavior == that.behavior &&
                Objects.equals(routingComponents, that.routingComponents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(template, behavior, routingComponents);
    }
}
