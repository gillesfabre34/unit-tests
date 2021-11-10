package com.airbus.retex.persistence.functionalArea;

import com.airbus.retex.model.functional.FunctionalAreaName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunctionalAreaNameRepository extends JpaRepository<FunctionalAreaName, Long>, JpaSpecificationExecutor<FunctionalAreaName> {
    List<FunctionalAreaName> findAll();
}
