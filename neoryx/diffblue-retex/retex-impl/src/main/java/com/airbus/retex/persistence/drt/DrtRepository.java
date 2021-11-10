package com.airbus.retex.persistence.drt;

import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.routing.Routing;
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

import java.util.Optional;

@Repository
public interface DrtRepository extends JpaRepository<Drt, Long>, CustomJpaRepository<Drt>, PagingAndSortingRepository<Drt, Long> {

    @Query(value = "SELECT COUNT(d.id) FROM Drt d WHERE d.routing = :routing")
    Integer countByRouting(@Param("routing") Routing routing);

    @Override
    @EntityGraph(attributePaths = {"childRequest.routing", "routing", "routing.operations.todoLists"})
    Optional<Drt> findById(Long id);

    @Override
    Page<Drt> findAll(Pageable pageable);

    Page<Drt> findAll(Specification<Drt> buildSpecification, Pageable pageable);
}
