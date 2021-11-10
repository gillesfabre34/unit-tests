package com.airbus.retex.model.admin.aircraft;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
public class AircraftType extends AbstractBaseModel {


    @NotNull
    @Column(name="name")
    private String name;


    @Column(name = "aircraft_family_id")
    private Long aircraftFamilyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_family_id", insertable = false, updatable = false)
    private AircraftFamily aircraftFamily;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "aircraftTypeId")
    private Set<AircraftVersion> aircraftVersions = new HashSet<>();

    public AircraftType(String name) {
        super();
        this.name = name;
    }

    public void addAircraftVersion(final AircraftVersion aircraftVersion){
        if(aircraftVersion != null){
            aircraftVersions.add(aircraftVersion);
            aircraftVersion.setAircraftType(this);
            aircraftVersion.setAircraftTypeId(this.getId());
        }
    }
}
