package com.airbus.retex.model.admin.airbus;

import com.airbus.retex.model.basic.AbstractBaseModelState;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the airbus_entity database table.
 */
@Entity
@Table(name = "airbus_entity")
@Audited
@Getter
@Setter
public class AirbusEntity extends AbstractBaseModelState {
    private static final long serialVersionUID = 8852841994717436665L;

    @Column(name = "code")
    private String code;

    @Column(name = "country_name")
    private String countryName;

}