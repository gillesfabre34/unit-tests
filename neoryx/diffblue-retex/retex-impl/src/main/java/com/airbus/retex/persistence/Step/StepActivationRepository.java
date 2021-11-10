package com.airbus.retex.persistence.Step;

import com.airbus.retex.model.step.StepActivation;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepActivationRepository extends JpaRepository<StepActivation, Long>, CustomJpaRepository<StepActivation> {

    boolean existsByStepTechnicalIdIn(List<Long> stepIds);
}
