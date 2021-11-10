package com.airbus.retex.persistence.childRequest;

import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildRequestRepository extends JpaRepository<ChildRequest, Long>, CustomJpaRepository<ChildRequest>, PagingAndSortingRepository<ChildRequest, Long> {

    @Query(value = "SELECT cr FROM ChildRequest cr WHERE cr.parentRequest.id = :requestId")
    @EntityGraph(attributePaths = {"routing", "routing.part", "medias", "environment", "missionType", "parentRequest"})
    Page<ChildRequest> findAll(@Param("requestId") Long requestId, Pageable pageable);

    /**
     * Get list of childRequest by specification
     * @param specification
     * @return
     */
    @EntityGraph(attributePaths = {"routing", "routing.part", "medias", "environment", "missionType", "parentRequest",
            "physicalParts", "aircraftFamily", "aircraftTypes", "aircraftVersions"})
    List<ChildRequest> findAll(Specification<ChildRequest> specification);

    @EntityGraph(attributePaths = {"clients", "physicalParts", "parentRequest", "medias", "routing", "routing.part", "aircraftTypes", "aircraftVersions"})
    Optional<ChildRequest> findByRoutingNaturalId(Long routingNaturalId);

    @EntityGraph(attributePaths = {"parentRequest"})
    List<ChildRequest> findByParentRequest(Request request);

    @EntityGraph(attributePaths = {"routing", "routing.part", "medias", "environment", "missionType", "parentRequest",
            "physicalParts", "aircraftFamily", "aircraftTypes", "aircraftVersions", "drts"})
    Optional<ChildRequest> findById(Long aLong);

    @Query(value = "SELECT COUNT(c.id) FROM ChildRequest c WHERE c.routingNaturalId = :routingNaturalId")
    Integer countByRoutingNaturalId(@Param("routingNaturalId") Long routingNaturalId);
}
