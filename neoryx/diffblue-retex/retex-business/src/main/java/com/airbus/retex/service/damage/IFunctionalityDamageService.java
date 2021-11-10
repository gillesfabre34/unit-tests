package com.airbus.retex.service.damage;


import com.airbus.retex.business.dto.damage.functionality.FunctionalityDamageCreationDto;
import com.airbus.retex.business.dto.damage.functionality.FunctionalityDamageDto;
import com.airbus.retex.business.exception.FunctionalException;

public interface IFunctionalityDamageService {
    /**
     * Create functionalityDamage
     * @param creationDto
     * @return
     * @throws FunctionalException
     */
    FunctionalityDamageDto createFunctionalityDamage(FunctionalityDamageCreationDto creationDto) throws FunctionalException;

    FunctionalityDamageDto getFunctionalityDamage(Long functionalDamageId, String isoCode);
}
