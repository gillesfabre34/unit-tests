package com.airbus.retex.persistence.operation;

import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationFunctionalAreaRepository extends JpaRepository<OperationFunctionalArea, Long>, CustomJpaRepository<OperationFunctionalArea> {

    @Query(value = "SELECT COUNT(*) FROM OperationFunctionalArea ofa " +
            "JOIN ofa.functionalArea fa " +
            "JOIN ofa.operation op " +
            "JOIN op.routing r " +
            "WHERE fa.disabled = false " +
            "AND r = :routing")
    int countAllOperationFunctionalAreasActivated(@Param("routing") Routing routing);
}
