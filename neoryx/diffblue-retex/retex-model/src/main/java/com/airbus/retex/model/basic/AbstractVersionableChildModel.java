package com.airbus.retex.model.basic;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.NaturalId;
import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Audited
@Getter
@Setter
public abstract class AbstractVersionableChildModel<T> implements Serializable, IIdentifiedVersionModel<T> {

    @NaturalId
    @Column(name = "natural_id")
    protected Long naturalId; // FIXME make

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected T technicalId;
}
