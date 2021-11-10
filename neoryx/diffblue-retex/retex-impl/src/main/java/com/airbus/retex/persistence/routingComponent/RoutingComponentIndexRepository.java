package com.airbus.retex.persistence.routingComponent;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.persistence.CustomJpaRepository;
import com.airbus.retex.persistence.VersionableRepository;

@Repository
public interface RoutingComponentIndexRepository extends VersionableRepository<RoutingComponentIndex, Long>, CustomJpaRepository<RoutingComponentIndex> {

    @Override
    @EntityGraph(attributePaths = {"todoList",
            "routingComponent",
            "todoList.operationType",
            "todoList.todoListName",
            "todoList.inspection",
            "routingComponent.operationType",
            "routingComponent.functionality",
            "routingComponent.inspection"
            })
    Page<RoutingComponentIndex> findAllLastVersions(Specification<RoutingComponentIndex> specification, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {
            "todoList",
            "routingComponent",
            "todoList.operationType",
            "todoList.todoListName",
            "todoList.inspection",
            "routingComponent.operationType",
            "routingComponent.functionality",
            "routingComponent.damage",
            "routingComponent.inspection",
            "routingComponent.steps"})
    Optional<RoutingComponentIndex> findLastVersionByNaturalId(Long id);

    @Override
    List<RoutingComponentIndex> findAllVersionsByNaturalId(Long entityId);

    @Query(value = "SELECT rci FROM RoutingComponentIndex rci " +
            "JOIN TodoList tl ON rci.todoList = tl.id WHERE tl.operationType.id = :operationTypeId AND rci.status = 'VALIDATED'")
    Set<RoutingComponentIndex> findTodoListByOperationTypeIdAndStatusIsValidated(@Param("operationTypeId") Long operationTypeId);


}
