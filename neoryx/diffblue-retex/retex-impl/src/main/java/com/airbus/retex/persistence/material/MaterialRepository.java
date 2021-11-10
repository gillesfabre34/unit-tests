package com.airbus.retex.persistence.material;

import com.airbus.retex.model.material.Material;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long>, CustomJpaRepository<Material> {

    Optional<Material> findByCode(String code);
}
