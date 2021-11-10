package com.airbus.retex.model.basic;

import com.airbus.retex.model.common.EnumActiveState;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Audited
@Getter
@Setter
public abstract class AbstractVersionableModelState extends AbstractVersionableModel<Long> {
    private static final long serialVersionUID = 1L;

    @Column
    @Enumerated(EnumType.STRING)
    private EnumActiveState state = EnumActiveState.ACTIVE;
}
