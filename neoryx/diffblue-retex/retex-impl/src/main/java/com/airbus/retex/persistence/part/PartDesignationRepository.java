package com.airbus.retex.persistence.part;


import com.airbus.retex.model.part.PartDesignation;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartDesignationRepository extends JpaRepository<PartDesignation, Long>, JpaSpecificationExecutor<PartDesignation>, CustomJpaRepository<PartDesignation> {

    /**
     *
     * @return List of PartDesignation
     */
    List<PartDesignation> findAll();

    /**
     * find one part designation by id
     * @param id
     * @return
     */
    Optional<PartDesignation> findById(Long id);
}
