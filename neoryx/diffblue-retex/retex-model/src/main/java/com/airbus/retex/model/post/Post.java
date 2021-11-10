package com.airbus.retex.model.post;

import com.airbus.retex.model.TranslatableModel;
import com.airbus.retex.model.basic.AbstractVersionableChildModel;
import com.airbus.retex.model.mesureUnit.MeasureUnit;
import com.airbus.retex.model.step.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Audited
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Post extends AbstractVersionableChildModel<Long> implements TranslatableModel<PostTranslation, PostFieldsEnum> {
    public static final String FIELD_DESIGNATION = "designation";

    @Column(name = "measure_unit_id")
    public Long measureUnitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measure_unit_id", referencedColumnName = "id", insertable = false, updatable = false)
    public MeasureUnit measureUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", referencedColumnName = "id")
    private Step step;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "entity")
    private Set<PostTranslation> translations = new HashSet<>();

    public void addTranslation(PostTranslation translation) {
        if (!translations.contains(translation)) {
            translation.setEntity(this);
            translations.add(translation);
        }
    }

    @Column(name = "is_deletable")
    public Boolean isDeletable;

    public void setDeletable(Boolean deletable) {
        if(null == isDeletable || Boolean.TRUE.equals(isDeletable)) isDeletable = deletable;
    }
}