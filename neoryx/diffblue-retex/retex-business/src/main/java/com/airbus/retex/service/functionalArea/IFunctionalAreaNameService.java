package com.airbus.retex.service.functionalArea;

import com.airbus.retex.business.dto.functionalAreaName.FunctionalAreaNameDto;
import com.airbus.retex.model.common.Language;

import java.util.List;

public interface IFunctionalAreaNameService {

    /**
     * Get All functional areas names
     * @param language
     * @return list functionalAreaNameDto
     */
    List<FunctionalAreaNameDto> getAllFunctionalAreasNames(Language language);
}
