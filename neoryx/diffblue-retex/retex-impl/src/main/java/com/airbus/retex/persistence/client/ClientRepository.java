package com.airbus.retex.persistence.client;

import com.airbus.retex.model.client.Client;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client>, CustomJpaRepository<Client> {

    /**
     * find all client associated to the given specification client
     * @param specification
     * @return list of clients
     */
    List<Client> findAll(Specification<Client> specification);

    @EntityGraph(attributePaths = "childRequests" )
    Optional<Client> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "childRequests" )
    boolean existsById(Long id);

    boolean existsByName(String name);

    Long countByIdIn(List<Long> ids);
}