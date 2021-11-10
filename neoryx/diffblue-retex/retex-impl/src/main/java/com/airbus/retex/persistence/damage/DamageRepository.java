package com.airbus.retex.persistence.damage;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.airbus.retex.model.damage.Damage;
import com.airbus.retex.persistence.CustomJpaRepository;
import com.airbus.retex.persistence.VersionableRepository;

@Repository
public interface DamageRepository
        extends VersionableRepository<Damage, Long>, CustomJpaRepository<Damage> {

    /**
     * find all damage associated to the given specification damage
     *
     * @param specification Specification
     * @return List of damages
     */
    @EntityGraph(attributePaths = {"damageValues"})
    List<Damage> findAll(Specification<Damage> specification);

    /**
     * @param specification Specification
     * @return List of damages
     */
    @EntityGraph(attributePaths = {"functionalityDamages", "classification"})
    Optional<Damage> findOne(Specification<Damage> specification);

    /**
     * find one damage by id
     * @param id
     * @return
     */
    @EntityGraph(attributePaths={"images", "functionalityDamages", "functionalityDamages.functionality", "classification"})
    Optional<Damage> findById(Long id);

    /**
     * find damage by functionality ID
     * @param id
     * @return
     */
    @Query(value = "SELECT d FROM Damage d LEFT JOIN d.functionalityDamages as fd LEFT JOIN fd.functionality as f WHERE f.id = :functionalityId And d.status = 'VALIDATED' AND d.state = 'ACTIVE'")
    @EntityGraph(attributePaths = {"functionalityDamages.functionality"})
    List<Damage> findByFunctionalityIdAndStateActive(@Param("functionalityId") Long id);

}
