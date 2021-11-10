package com.airbus.retex.persistence.mission;

import com.airbus.retex.model.mission.MissionType;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionTypeRepository extends JpaRepository<MissionType, Long>, JpaSpecificationExecutor<MissionType>, CustomJpaRepository<MissionType> {

    /**
     * @return List of Mission Type
     */
    List<MissionType> findAll();
}
