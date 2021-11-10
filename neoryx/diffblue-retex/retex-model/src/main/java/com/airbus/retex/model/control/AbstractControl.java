package com.airbus.retex.model.control;

import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.drt.Drt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Audited
@Entity(name = "control")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "operation_discr")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractControl<T> extends AbstractBaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drt_id")
    protected Drt drt;

    public abstract T getValue();
}
