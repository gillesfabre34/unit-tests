package com.airbus.retex.model.operation;

import com.airbus.retex.model.basic.AbstractVersionableChildModel;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.step.StepActivation;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
public class OperationFunctionalArea extends AbstractVersionableChildModel<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id", referencedColumnName = "id")
    private Operation operation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functional_area_id", referencedColumnName = "id")
    private FunctionalArea functionalArea;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "operationFunctionalArea")
    private Set<StepActivation> stepActivations = new HashSet<>();

    public void addStepActivation(StepActivation stepActivation) {
        stepActivation.setOperationFunctionalArea(this);
        this.stepActivations.add(stepActivation);
    }
}
