package com.airbus.retex.persistence.admin;

import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role>, CustomJpaRepository<Role> {

    /**
     * @param specification Specification
     * @return List of RoleLabel
     */
    @EntityGraph(attributePaths = {"features"})
    //, "roleLabels", "features.feature.featureLabels"
    List<Role> findAll(@Nullable Specification<Role> specification);

    /**
     * return list of Roles by airbusEntityId
     *
     * @param idAirbusEntity
     * @return
     */
    List<Role> findByAirbusEntityId(@Param("id_airbus_entity") Long idAirbusEntity);


    /**
     * @param roleId
     * @return number of updated lignes
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE Role r SET r.state = 'REVOKED' WHERE r.id = :roleId")
    int revokeRole(@Param("roleId") Long roleId);

    /**
     * @param roleId
     * @return number of updated lignes
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE Role r SET r.state = 'REVOKED' WHERE r.id = :roleId AND r.airbusEntity.id = :airbusEntityId")
    int revokeRole(@Param("roleId") Long roleId, @Param("airbusEntityId") Long airbusEntityId);

    /**
     * Find Roles with list of ids
     *
     * @param roles - role ids
     * @return collection of Role
     */
    List<Role> findByIdIn(List<Long> roles);

    @Query(value = "SELECT r FROM Role r WHERE r.id = :id")
    @EntityGraph(attributePaths = {"features"})
    Role getOne(@Param("id") Long id);

    @EntityGraph(attributePaths = {"features", "airbusEntity"})
    Optional<Role> findById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"features", "airbusEntity"})
    @Transactional
    List<Role> findAll();
}
