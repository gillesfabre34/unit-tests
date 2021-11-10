package com.airbus.retex.persistence.damage.functionality;

import com.airbus.retex.model.functionality.damage.FunctionalityDamage;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FunctionalityDamageRepository extends JpaRepository<FunctionalityDamage, Long>, JpaSpecificationExecutor<FunctionalityDamage> {
    /**
     * retreive all functional damages assocated to given specifiation damage
     *
     * @param specification Specification
     * @return List of functions
     */
    @EntityGraph(attributePaths = {"functionalityDamageInfo"})
    List<FunctionalityDamage> findAll(@Nullable Specification<FunctionalityDamage> specification);

    /**
     * find a functionalDamage associated to given Specification damage
     *
     * @param specification
     * @return
     */
    @EntityGraph(
            attributePaths = {
                    "functionality",
                    "functionality.functionalityValues",
                    "functionalityDamageInfo",
                    "functionalityDamageInfo.functionalityDamageValues"
            })
    Optional<FunctionalityDamage> findOne(Specification<FunctionalityDamage> specification);

    /**
     * Get a damage
     * @param id
     * @return
     */
    @EntityGraph(attributePaths = { "functionality", "damage", "image" } )
    Optional<FunctionalityDamage> findById(Long id);
}
