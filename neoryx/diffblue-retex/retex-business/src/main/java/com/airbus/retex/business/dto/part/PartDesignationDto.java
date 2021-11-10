package com.airbus.retex.business.dto.part;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.business.dto.TranslatedDto;
import com.airbus.retex.model.part.PartDesignation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartDesignationDto extends TranslatedDto {

    private Long id;

    private final TranslateDto designation = new TranslateDto(this, PartFieldsEnum.designation);

    @Override
    public String getClassName() {
        return PartDesignation.class.getSimpleName();
    }

    @Override
    public Long getEntityId() {
        return id;
    }
}
