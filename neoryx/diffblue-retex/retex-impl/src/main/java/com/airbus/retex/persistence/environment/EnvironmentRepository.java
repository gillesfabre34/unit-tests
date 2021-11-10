package com.airbus.retex.persistence.environment;

import com.airbus.retex.model.environment.Environment;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, Long>, JpaSpecificationExecutor<Environment>, CustomJpaRepository<Environment> {

    /**
     * @return List of Environment
     */
    List<Environment> findAll();
}
