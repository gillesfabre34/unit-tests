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
@DiscriminatorValue("Visual")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControlVisual extends AbstractControlRoutingComponent<Boolean> {
    @Column(name = "visual_value")
    private Boolean value;
}
