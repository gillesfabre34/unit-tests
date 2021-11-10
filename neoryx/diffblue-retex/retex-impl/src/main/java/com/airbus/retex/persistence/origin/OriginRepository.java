package com.airbus.retex.persistence.origin;

import com.airbus.retex.model.origin.Origin;
import com.airbus.retex.persistence.IRepository;

import java.util.Optional;

public interface OriginRepository extends IRepository<Origin, Long> {
    Optional<Origin> findByName(String name);
}
