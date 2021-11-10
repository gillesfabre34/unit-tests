package com.airbus.retex.persistence.admin;

import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.user.UserFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFeatureRepository  extends JpaRepository <UserFeature, Long>{

    @Query(value = "SELECT uf FROM UserFeature uf WHERE uf.user.id = :idUser AND uf.code = :featureCode")
    Optional<UserFeature> findUserFeature(@Param("idUser") Long idUser, @Param("featureCode") FeatureCode featureCode);

    List<UserFeature> findAllByUserId(Long userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM UserFeature uf WHERE uf.user.id = :idUser AND uf.code = :featureCode")
    void delete(@Param("idUser") Long idUser, @Param("featureCode") FeatureCode featureCode);
}
