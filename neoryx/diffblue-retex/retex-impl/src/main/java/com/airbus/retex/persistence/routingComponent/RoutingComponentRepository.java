package com.airbus.retex.persistence.routingComponent;

import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutingComponentRepository extends JpaRepository<RoutingComponent, Long>, CustomJpaRepository<RoutingComponent> {

    @EntityGraph(attributePaths = {"operationType.todoList.todoListName","functionality"})
    Page<RoutingComponent> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"damage", "operationType", "functionality", "inspection"})
    Optional<RoutingComponent> findByNaturalId(Long id);

    Boolean existsByNaturalIdAndOperationTypeId(Long id, Long operationTypeId);

    Boolean existsByOperationTypeIdAndInspectionIdAndFunctionalityIdAndDamageIdAndNaturalIdNot(Long operationTypeId, Long inspectionId, Long functionalityId, Long damageId, @Nullable Long id);

    Boolean existsByDamageId(Long damageId);

    Boolean existsByDamageIdAndFunctionalityId(Long damageId, Long functionalityId);
}
