package com.airbus.retex.model.routingComponent;

import com.airbus.retex.model.AbstractTranslation;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Getter
@Setter
public class RoutingComponentIndexTranslation extends AbstractTranslation<RoutingComponentFieldsEnum> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", referencedColumnName = "id", nullable = false)
    private RoutingComponentIndex entity;
}
