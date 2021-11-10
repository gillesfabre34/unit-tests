package com.airbus.retex.persistence.translate;

import com.airbus.retex.model.translation.Translate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TranslateRepository extends JpaRepository<Translate, Translate.TranslateId>, JpaSpecificationExecutor<Translate> {
    /**
     * find list of translates of the given identifier
     *
     * @param translateId
     * @return
     */
    Optional<Translate> findById(Translate.TranslateId translateId);
}
