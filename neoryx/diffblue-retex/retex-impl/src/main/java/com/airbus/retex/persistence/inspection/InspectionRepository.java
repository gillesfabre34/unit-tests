package com.airbus.retex.persistence.inspection;

import com.airbus.retex.model.inspection.Inspection;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long>, CustomJpaRepository<Inspection> {

    /**
     * @param value
     * @return
     */
    Optional<Inspection> findByValue(String value);

    List<Inspection> findAll(Specification<Inspection> specification);
}
