package com.airbus.retex.persistence.admin;

import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.role.RoleFeature;
import com.airbus.retex.model.common.EnumRightLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleFeatureRepository extends JpaRepository<RoleFeature, Long> {

    @Transactional//FIXME why ?
    @Modifying
    @Query(value = "DELETE FROM RoleFeature rf WHERE rf.roleId = :idRole AND rf.code = :code")
    void delete(@Param("idRole") Long idRole, @Param("code") FeatureCode code);

    Optional<List<RoleFeature>> findByRoleId(@Param("idRole") Long idRole);

    @Query(value = "SELECT uf FROM RoleFeature uf WHERE uf.role.id = :idRole AND uf.code = :featureCode")
    Optional<RoleFeature> findRoleFeature(@Param("idRole") Long idRole, @Param("featureCode") FeatureCode featureCode);

    @Query(value = "SELECT uf FROM RoleFeature uf WHERE uf.role.id = :idRole AND uf.code = :featureCode AND uf.rightLevel = :rightLevel")
    Optional<RoleFeature> findRoleFeature(@Param("idRole") Long idRole, @Param("featureCode") FeatureCode featureCode, @Param("rightLevel") EnumRightLevel rightLevel);
}
