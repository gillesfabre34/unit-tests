package com.airbus.retex.service.part;

import com.airbus.retex.business.dto.part.PartDesignationLightDto;
import com.airbus.retex.model.common.Language;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IPartDesignationService {

    /**
     * Get all parts designation
     *
     * @return
     */
    List<PartDesignationLightDto> getAllPartsDesignation(Language language);
}
