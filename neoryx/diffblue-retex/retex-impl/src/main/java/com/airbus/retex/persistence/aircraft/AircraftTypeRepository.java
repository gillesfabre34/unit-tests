package com.airbus.retex.persistence.aircraft;

import com.airbus.retex.model.admin.aircraft.AircraftType;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AircraftTypeRepository extends JpaRepository<AircraftType, Long>, JpaSpecificationExecutor<AircraftType>, CustomJpaRepository<AircraftType> {

    /**
     * @return List of Aircraft Type for a given aircraft family
     * @param aircraftFamilyId
     */
    List<AircraftType> findByAircraftFamilyId(Long aircraftFamilyId);
}
