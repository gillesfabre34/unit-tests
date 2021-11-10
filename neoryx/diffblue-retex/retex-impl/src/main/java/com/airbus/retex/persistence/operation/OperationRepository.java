package com.airbus.retex.persistence.operation;

import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long>, CustomJpaRepository<Operation> {

    @EntityGraph(attributePaths = {"operationType", "routing", "todoLists", "todoLists.todoListName"})
    List<Operation> findOperationByRoutingTechnicalId(Long id);
}
