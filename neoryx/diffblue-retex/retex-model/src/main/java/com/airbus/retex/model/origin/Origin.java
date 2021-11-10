package com.airbus.retex.model.origin;

import com.airbus.retex.model.basic.AbstractBaseModel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;

@Entity
@Audited
@Getter
@Setter
public class Origin extends AbstractBaseModel {

    private String name;

    private String color;
}