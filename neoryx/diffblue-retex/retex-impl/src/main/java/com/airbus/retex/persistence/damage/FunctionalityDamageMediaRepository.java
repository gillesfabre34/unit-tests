package com.airbus.retex.persistence.damage;

import com.airbus.retex.model.functionality.damage.FunctionalityDamageMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunctionalityDamageMediaRepository extends JpaRepository<FunctionalityDamageMedia, Long> {
}