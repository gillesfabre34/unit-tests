package com.airbus.retex.persistence.aircraft;

import com.airbus.retex.model.admin.aircraft.AircraftFamily;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AircraftFamilyRepository extends JpaRepository<AircraftFamily, Long>, JpaSpecificationExecutor<AircraftFamily>, CustomJpaRepository<AircraftFamily> {

    Optional<AircraftFamily> findByName(String name);
}
