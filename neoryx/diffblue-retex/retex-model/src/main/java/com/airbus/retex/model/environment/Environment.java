package com.airbus.retex.model.environment;

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

@Entity
@Audited
@Getter
@Setter
public class Environment extends AbstractBaseModel {

    @OneToMany(mappedBy = "entityId", fetch = FetchType.LAZY)
    @Where(clause = "class_name = 'Environment'")
    private List<Translate> translates  = new ArrayList<>();
}
