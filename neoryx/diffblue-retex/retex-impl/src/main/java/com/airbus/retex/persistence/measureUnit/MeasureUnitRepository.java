package com.airbus.retex.persistence.measureUnit;

import com.airbus.retex.model.mesureUnit.MeasureUnit;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasureUnitRepository extends JpaRepository<MeasureUnit, Long>, CustomJpaRepository<MeasureUnit> {

    List<MeasureUnit> findAll(Specification<MeasureUnit> specification);
}
