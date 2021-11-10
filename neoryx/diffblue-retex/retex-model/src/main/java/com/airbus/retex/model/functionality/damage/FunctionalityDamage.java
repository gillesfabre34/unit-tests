package com.airbus.retex.model.functionality.damage;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.envers.Audited;

import com.airbus.retex.model.TranslatableModel;
import com.airbus.retex.model.basic.AbstractVersionableChildModel;
import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.model.damage.Damage;
import com.airbus.retex.model.functionality.Functionality;
import com.airbus.retex.model.media.Media;

import lombok.Getter;
import lombok.Setter;

@Entity
@Audited
@Getter
@Setter
public class FunctionalityDamage extends AbstractVersionableChildModel<Long> implements TranslatableModel<FunctionalityDamageTranslation, FunctionalityDamageFieldsEnum> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_damage", nullable = false)
    private Damage damage;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_functionality", nullable = false)
    private Functionality functionality;

    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", nullable = true)
    private Media image;

    @Column
    @Enumerated(EnumType.STRING)
    private EnumActiveState state = EnumActiveState.ACTIVE;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "entity")
    private Set<FunctionalityDamageTranslation> translations = new HashSet<>();

    @Override
    public void addTranslation(FunctionalityDamageTranslation translation) {
        translation.setEntity(this);
        translations.add(translation);
    }
}
