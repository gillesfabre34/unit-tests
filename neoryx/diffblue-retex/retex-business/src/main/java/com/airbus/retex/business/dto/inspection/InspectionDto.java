package com.airbus.retex.business.dto.inspection;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.business.dto.TranslatedDto;
import com.airbus.retex.model.inspection.Inspection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InspectionDto extends TranslatedDto {
    private Long id;

    private final TranslateDto name = new TranslateDto(this, InspectionEnumFields.name);

    private String value;

    @Override
    public String getClassName() {
        return Inspection.class.getSimpleName();
    }

    @Override
    public Long getEntityId() {
        return id;
    }
}
