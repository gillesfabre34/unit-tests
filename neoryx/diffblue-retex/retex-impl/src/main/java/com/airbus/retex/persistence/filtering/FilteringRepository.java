package com.airbus.retex.persistence.filtering;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.persistence.CustomJpaRepository;

@Repository
public interface FilteringRepository extends JpaRepository<Filtering, Long>, CustomJpaRepository<Filtering>, PagingAndSortingRepository<Filtering, Long> {
    /**
     * find all filtering associated to the given specification filtering
     *
     * @return List of filterings
     */
    @EntityGraph(attributePaths = {"physicalPart.part.partNumber","physicalPart.serialNumber"})
    Page<Filtering> findAll(Specification<Filtering> specification, Pageable pageable);



    @Override
    @EntityGraph(attributePaths = {"physicalPart", "physicalPart.part", "physicalPart.childRequest.parentRequest.origin", "drt","medias"})
    Optional<Filtering> findById(Long id);

    Optional<Filtering> findByPhysicalPart(PhysicalPart physicalPart);
}
