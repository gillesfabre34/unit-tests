package com.airbus.retex.persistence.functionality;

import com.airbus.retex.model.functionality.Functionality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunctionalityRepository extends JpaRepository<Functionality, Long>, JpaSpecificationExecutor<Functionality> {

    /**
     *  find all functionalities
     * @return
     */
    List<Functionality> findAll();

    /**
     * find functionality by operation type id
     * @param operationTypeId
     * @return
     */
    @Query(value = "SELECT f FROM Functionality as f LEFT JOIN f.routingComponents as r LEFT JOIN r.operationType as o WHERE o.id = :operationType")
    List<Functionality> findByOperationTypeId(@Param("operationType")Long operationTypeId);
}
