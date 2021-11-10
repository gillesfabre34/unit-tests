package com.airbus.retex.persistence.routing;

import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.persistence.VersionableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutingRepository extends VersionableRepository<Routing, Long>, JpaRepository<Routing, Long>, PagingAndSortingRepository<Routing, Long> {

    /**
     * find all routing  associated to the given specification routing
     *
     * @return List of routing
     */
    @EntityGraph(attributePaths = {"creator", "part"})
    Page<Routing> findAll(Specification<Routing> specification, Pageable pageable);

    /**
     * Returns whether a Routing with the given partId exists.
     *
     * @return boolean
     */
    boolean existsByPartTechnicalId(Long id);

    @Override
    @EntityGraph(attributePaths = {"part", "part.partDesignation", "creator"})
    Optional<Routing> findLastVersionByNaturalId(Long id);

    @EntityGraph(attributePaths = {"part", "creator"})
    Optional<Routing> findByPartTechnicalIdAndIsLatestVersionTrue(Long partId);

    @EntityGraph(attributePaths = {"part", "creator"})
    Optional<Routing> findByPartNaturalIdAndStatus(Long partId, EnumStatus status);
}
