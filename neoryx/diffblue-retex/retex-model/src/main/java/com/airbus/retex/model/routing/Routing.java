package com.airbus.retex.model.routing;

import com.airbus.retex.model.TranslatableModel;
import com.airbus.retex.model.basic.AbstractVersionableModel;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.user.User;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
public class Routing extends AbstractVersionableModel<Long> implements TranslatableModel<RoutingTranslation, RoutingFieldsEnum> {

    public static final String FIELD_NAME = "name";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Part part;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "routing", orphanRemoval = true)
    private Set<Operation> operations = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "entity")
    private Set<RoutingTranslation> translations = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "routing")
    private Set<Drt> drts = new HashSet<>();

    public void addTranslation(RoutingTranslation translation) {
        translation.setEntity(this);
        translations.add(translation);
    }

    public void addDrt(Drt drt) {
        drt.setRouting(this);
        this.drts.add(drt);
    }

    public void addOperation(Operation operation) {
        operation.setRouting(this);
        operations.add(operation);
    }
}
