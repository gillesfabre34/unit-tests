package com.airbus.retex.model.damage;

import com.airbus.retex.model.TranslatableModel;
import com.airbus.retex.model.basic.AbstractVersionableModelState;
import com.airbus.retex.model.classification.EnumClassification;
import com.airbus.retex.model.functionality.damage.FunctionalityDamage;
import com.airbus.retex.model.media.Media;
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
public class Damage extends AbstractVersionableModelState implements TranslatableModel<DamageTranslation, DamageFieldsEnum> {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "damage")
    private Set<FunctionalityDamage> functionalityDamages;

    @Column(name = "classification")
    @Enumerated(EnumType.STRING)
    private EnumClassification classification;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "damage_media",
            joinColumns = {@JoinColumn(name = "damage_id")},
            inverseJoinColumns = {@JoinColumn(name = "media_id")}
    )
    @OrderBy(value="createdAt")
    private Set<Media> images = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "entity")
    private Set<DamageTranslation> translations = new HashSet<>();

    public void addTranslation(DamageTranslation translation) {
        translation.setEntity(this);
        translations.add(translation);
    }

    public void addImage(Media image) {
        this.images.add(image);
    }
}
