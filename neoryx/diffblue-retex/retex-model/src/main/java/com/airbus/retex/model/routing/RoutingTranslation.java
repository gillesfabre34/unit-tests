package com.airbus.retex.model.routing;

import com.airbus.retex.model.AbstractTranslation;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Audited
public class RoutingTranslation extends AbstractTranslation<RoutingFieldsEnum> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", referencedColumnName = "id", nullable = false)
    private Routing entity;
}
