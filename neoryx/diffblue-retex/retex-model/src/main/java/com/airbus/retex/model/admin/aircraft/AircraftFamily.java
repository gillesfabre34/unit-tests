package com.airbus.retex.model.admin.aircraft;

import com.airbus.retex.model.basic.AbstractBaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AircraftFamily extends AbstractBaseModel {

    @NotNull
    @Column(name="name")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "aircraftFamilyId")
    private Set<AircraftType> aircraftTypes=new HashSet<>();

    public AircraftFamily(String name) {
        super();
        this.name = name;
    }


    public void addAircraftType(final AircraftType aircraftType) {
        if (aircraftType != null) {
            aircraftTypes.add(aircraftType);
            aircraftType.setAircraftFamily(this);
            aircraftType.setAircraftFamilyId(this.getId());
        }
    }
}
