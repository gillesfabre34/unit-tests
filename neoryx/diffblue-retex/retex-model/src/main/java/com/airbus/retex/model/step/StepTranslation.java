package com.airbus.retex.model.step;

import com.airbus.retex.model.AbstractTranslation;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Audited
@Getter
@Setter
public class StepTranslation extends AbstractTranslation<StepFieldsEnum> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", referencedColumnName = "id", nullable = false)
    private Step entity;
}
