package com.airbus.retex.persistence.treatment;

import com.airbus.retex.model.treatment.Treatment;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long>, CustomJpaRepository<Treatment> {

    List<Treatment> findAll(Specification<Treatment> specification);
}
