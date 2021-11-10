package com.airbus.retex.persistence.functionalArea;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.part.Part;

@Repository
public interface FunctionalAreaRepository extends JpaRepository<FunctionalArea, Long>, JpaSpecificationExecutor<FunctionalArea> {


    @Override
    List<FunctionalArea> findAll();

    List<FunctionalArea> findByPart(Part part);

    @Query(value = "SELECT fa FROM FunctionalArea fa WHERE fa.areaNumber = :areaNumber and fa.part = :id")
    Optional<FunctionalArea> findByAreaNumberAndPartId(@Param("areaNumber") String areaNumber, @Param("id") Long id);

    Optional<FunctionalArea> findByNaturalId(Long naturalId);

    @EntityGraph(attributePaths = {"functionality", "functionality.routingComponents"})
    Optional<FunctionalArea> findByTechnicalId(Long naturalId);

    @Query(value = FunctionalAreaRepositoryConstant.QUERY_FIND_BY_ROUTING)
    @EntityGraph(attributePaths = {"functionality", "treatment", "areaNumber", "functionalAreaName"})
    List<FunctionalArea> findByRoutingId(@Param("routingId") Long routingId);

    @Query(value = FunctionalAreaRepositoryConstant.QUERY_FIND_BY_ROUTING + "AND fa.disabled = false")
    @EntityGraph(attributePaths = {"functionality", "treatment", "areaNumber", "functionalAreaName"})
    List<FunctionalArea> findByRoutingIdAndFunctionalAreasEnabled(@Param("routingId") Long routingId);

}
