package com.airbus.retex.persistence.airbus;

import com.airbus.retex.model.admin.airbus.AirbusEntity;
import com.airbus.retex.persistence.IRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirbusEntityRepository extends IRepository<AirbusEntity, Long> {

    List<AirbusEntity> findAll();
    Optional<AirbusEntity> findById(Long id);

    boolean existsByCodeAndCountryName(String code, String countryName);

}
