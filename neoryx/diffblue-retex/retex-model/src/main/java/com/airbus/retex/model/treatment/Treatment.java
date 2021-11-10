package com.airbus.retex.model.treatment;

import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.translation.Translate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Audited
public class Treatment extends AbstractBaseModel {
    public static final String FIELD_NAME = "name";

    @OneToMany(mappedBy = "entityId", fetch = FetchType.LAZY)
    @Where(clause = "class_name = 'Treatment'")
    private List<Translate> translates  = new ArrayList<>();
}
