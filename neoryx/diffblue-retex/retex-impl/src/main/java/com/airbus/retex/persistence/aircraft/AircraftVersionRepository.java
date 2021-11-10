package com.airbus.retex.persistence.aircraft;

import com.airbus.retex.model.admin.aircraft.AircraftVersion;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface AircraftVersionRepository extends JpaRepository<AircraftVersion, Long>, JpaSpecificationExecutor<AircraftVersion>, CustomJpaRepository<AircraftVersion> {

    List<AircraftVersion> findByAircraftTypeIdIn(Collection<Long> aircraftTypeIds);
}
