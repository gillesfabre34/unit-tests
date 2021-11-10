package com.airbus.retex.persistence.ata;

import com.airbus.retex.model.ata.ATA;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ATARepository extends JpaRepository<ATA, Long>, CustomJpaRepository<ATA> {
    /**
     * @return List of ATA
     */
    @Query(value = "SELECT code FROM ata", nativeQuery = true)
    List<String> getAllATACode();

    Optional<ATA> findByCode(String code);
}
