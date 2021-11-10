package com.airbus.retex.persistence.part;

import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.persistence.CustomJpaRepository;
import com.airbus.retex.persistence.VersionableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartRepository extends VersionableRepository<Part, Long>, CustomJpaRepository<Part>, PagingAndSortingRepository<Part, Long> {

    /**
     * find all part associated to the given specification part
     *
     * @return List of parts
     */
    @EntityGraph(attributePaths = {"partDesignation"})
    Page<Part> findAll(Specification<Part> specification, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"functionalAreas"})
    Optional<Part> findById(Long id);

    /**
     * find all parts filter by a PartNumber
     *
     * @param partNumber
     * @return List of parts filtered by PartNumber
     */
    @EntityGraph(attributePaths = {"functionalAreas.functionality.routingComponents.operationType"})
    Optional<Part> findPartByPartNumberAndIsLatestVersionTrue(String partNumber);

    /**
     * find all parts filter by a PartNumberRoot
     * @param partNumberRoot
     * @return List of parts filtered by PartNumberRoot
     */
    @EntityGraph(attributePaths = {"functionalAreas.functionality.routingComponents.operationType"})
    List<Part> findPartByPartNumberRootAndStatus(String partNumberRoot, EnumStatus status);

    /**
     * find all parts by a PartNumber and a PartNumberRoot
     * @param partNumber
     * @param partNumberRoot
     * @return List of parts filtered by PartNumber and PartNumberRoot
     */
    Optional<Part> findPartByPartNumberAndPartNumberRootAndIsLatestVersionTrue(String partNumber,String partNumberRoot);

    @Query(value = "SELECT DISTINCT p " +
            "FROM Part p LEFT JOIN FunctionalArea fa ON fa.part = p " +
            "WHERE fa.part IS NOT NULL " +
            "AND p.partNumber LIKE CONCAT('%', :searchValue, '%') " +
            "AND p.partNumber <> '' " +
            "AND p.status = :status ")
    Page<Part> findByPartNumberStartingWithAndFunctionalAreasIsNotNull(@Param("searchValue")String searchValue, Pageable pageable, @Param("status") EnumStatus status);

    @Query(value = "SELECT DISTINCT p " +
            "FROM Part p " +
            "LEFT JOIN Routing r ON r.part = p.id " +
            "LEFT JOIN FunctionalArea fa ON fa.part = p " +
            "WHERE r.part IS NULL " +
            "AND fa.part IS NOT NULL " +
            "AND p.partNumber LIKE CONCAT('%', :searchValue, '%') " +
            "AND p.partNumber <> '' " +
            "AND p.status = :status ")
    Page<Part> findByPartNumberStartingWithAndFunctionalAreasIsNotNullNotInRouting(@Param("searchValue")String searchValue , Pageable pageable, @Param("status") EnumStatus status);

    @Query(value = "SELECT DISTINCT p.partNumberRoot  " +
            "FROM Part p " +
            "LEFT JOIN Routing r ON r.part = p.id " +
            "LEFT JOIN FunctionalArea fa ON fa.part = p " +
            "WHERE r.part IS NULL " +
            "AND fa.part IS NOT NULL " +
            "AND p.partNumberRoot LIKE CONCAT('%', :searchValue, '%') " +
            "AND p.partNumberRoot <> '' " +
            "AND p.status = :status ")
    Page<String> findByPartNumberRootStartingWith(@Param("searchValue")String searchValue, Pageable pageable, @Param("status") EnumStatus status);

}
