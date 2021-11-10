package com.airbus.retex.persistence.admin;

import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, CustomJpaRepository<User> {

    @EntityGraph(attributePaths = {"roles"})
    Page<User> findAll(Specification specification, Pageable pageable);

    @EntityGraph(attributePaths = {"airbusEntity", "roles", "userFeatures"})
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"airbusEntity", "roles", "requests"})
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = {"roles", "userFeatures"})
    User getById(Long id);

    @Query(value = "SELECT u FROM User u WHERE u.id = :id")
    @EntityGraph(attributePaths = {"airbusEntity", "roles"})
    User getOne(@Param("id") Long id);

    List<User> findAll();
}
