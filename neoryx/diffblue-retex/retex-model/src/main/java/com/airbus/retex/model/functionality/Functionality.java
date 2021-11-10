package com.airbus.retex.model.functionality;

import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.translation.Translate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Example : teeth, bearingRace ...etc
 * ça represente une fonctionnalité d'une pièce
 * chaque fonctional_area (zone de part-mapping) et relié à une et
 * unique fonctionalité
 */
@Entity
@Audited
@Getter
@Setter
public class Functionality extends AbstractBaseModel {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "functionality")
    private Set<RoutingComponent> routingComponents = new HashSet<>();

    /**
     * @param component
     */
    public void addRoutingComponent(RoutingComponent component) {
        component.setFunctionality(this);
        this.routingComponents.add(component);
    }

    @OneToMany(mappedBy = "entityId", fetch = FetchType.LAZY)
    @Where(clause = "class_name = 'Functionality'")
    private List<Translate> translates  = new ArrayList<>();
}
