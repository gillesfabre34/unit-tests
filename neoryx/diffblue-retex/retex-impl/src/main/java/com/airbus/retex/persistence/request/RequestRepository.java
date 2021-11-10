package com.airbus.retex.persistence.request;

import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long>, CustomJpaRepository<Request>, PagingAndSortingRepository<Request, Long> {

    List<Request> findAll(Specification<Request> specification);

    @EntityGraph(attributePaths = {"airbusEntity", "requester", "ata", "origin"})
    Page<Request> findAll(Specification<Request> buildSpecification, Pageable pageable);

    @Query("select distinct r.requester from Request r")
    List<User> getAllRequesters();

    @EntityGraph(attributePaths = {"technicalResponsibles", "operators", "clients", "originMedias", "specMedias",
            "environment", "missionType", "aircraftVersions", "aircraftTypes", "aircraftFamily", "childRequests"})
    Optional<Request> findById(Long id);
}
