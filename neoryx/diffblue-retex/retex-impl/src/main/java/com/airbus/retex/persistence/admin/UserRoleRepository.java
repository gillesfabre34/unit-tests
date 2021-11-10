package com.airbus.retex.persistence.admin;

import com.airbus.retex.model.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query(value = "SELECT ur FROM UserRole ur WHERE ur.user.id = :idUser AND ur.role.id = :idRole")
    Optional<UserRole> findUserRoles(@Param("idUser") Long idUser, @Param("idRole") Long idRole);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.user.id = :idUser AND ur.role.id = :idRole")
    void delete(@Param("idUser") Long idUser, @Param("idRole") Long idRole);
}
