package com.airbus.retex.model.admin.aircraft;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.airbus.retex.model.basic.AbstractBaseModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
public class AircraftVersion extends AbstractBaseModel {

    @NotNull
    @Column(name="name")
    private String name;


    @Column(name = "aircraft_type_id")
    private Long aircraftTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_type_id", insertable = false, updatable = false)
    private AircraftType aircraftType;

    public AircraftVersion(String name) {
        super();
        this.name = name;
    }
}
