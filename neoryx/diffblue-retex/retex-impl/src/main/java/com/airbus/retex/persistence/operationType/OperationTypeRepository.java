package com.airbus.retex.persistence.operationType;

import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperationTypeRepository extends JpaRepository<OperationType, Long>, CustomJpaRepository<OperationType> {

    Optional<OperationType> findOne(Specification<OperationType> specification);

    Optional<OperationType> findByTemplate(String template);
}
