package com.airbus.retex.persistence.mpn;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.airbus.retex.model.part.Mpn;
import com.airbus.retex.persistence.CustomJpaRepository;

@Repository
public interface MpnRepository extends JpaRepository<Mpn, Long>, CustomJpaRepository<Mpn> {

    Optional<Mpn> findByCode(String code);
}
