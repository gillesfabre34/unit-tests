package com.airbus.retex.model;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.common.Language;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

@MappedSuperclass
@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractTranslation<E extends Enum> extends AbstractBaseModel {

    @Column(name = "language")
    @Enumerated(value = EnumType.STRING)
    protected Language language;

    @Column(name = "field")
    @Enumerated(value = EnumType.STRING)
    protected E field;

    @Column(name = "value")
    protected String value;


}
