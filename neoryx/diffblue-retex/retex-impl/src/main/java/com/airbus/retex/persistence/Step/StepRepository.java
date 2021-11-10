package com.airbus.retex.persistence.Step;

import com.airbus.retex.model.step.Step;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StepRepository extends JpaRepository<Step, Long>, CustomJpaRepository<Step> {
    /**
     * find step by stepId
     * @param stepId
     * @return
     */
    Optional<Step> findByNaturalId(long stepId);

}
