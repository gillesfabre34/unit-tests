package com.airbus.retex.model.control;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Audited
@DiscriminatorValue("RoutingComponent")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControlRoutingComponent extends AbstractControlRoutingComponent<Float> {
    @Column(name = "routing_component_value")
    private Float value;
}
