package com.airbus.retex.model.routingComponent;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.JoinFormula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.airbus.retex.model.IRoutingComponentModel;
import com.airbus.retex.model.basic.AbstractVersionableChildModel;
import com.airbus.retex.model.damage.Damage;
import com.airbus.retex.model.functionality.Functionality;
import com.airbus.retex.model.inspection.Inspection;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.step.Step;

import lombok.Getter;
import lombok.Setter;

@Entity
@Audited
@Getter
@Setter
public class RoutingComponent extends AbstractVersionableChildModel<Long> implements IRoutingComponentModel {

    @Column(name = "damage_id")
    private Long damageId;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula(value = "(SELECT d.id FROM damage d WHERE d.natural_id = damage_id AND d.status = 'VALIDATED')")
    private Damage damage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_type_id")
    private OperationType operationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functionality_id")
    private Functionality functionality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id")
    private Inspection inspection;

    // RoutingComponent contains many Steps
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "routingComponent")
    private List<Step> steps = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "routingComponent")
    private RoutingComponentIndex routingComponentIndex;

    public void setSteps(List<Step> steps){
        this.steps = steps;
        steps.forEach(step -> step.setRoutingComponent(this));
    }

    public void addStep(Step step){
        step.setRoutingComponent(this);
        this.steps.add(step);
    }

    public void setDamage(Damage damage){
        if (null != damage) {
            this.damage = damage;
            this.setDamageId(damage.getNaturalId());
        }
    }

}
