package com.airbus.retex.model.damage;

import com.airbus.retex.model.AbstractTranslation;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Getter
@Setter
public class DamageTranslation extends AbstractTranslation<DamageFieldsEnum> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", referencedColumnName = "id", nullable = false)
    private Damage entity;

}
