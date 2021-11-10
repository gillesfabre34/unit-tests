package com.airbus.retex.business.dto.measureUnit;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.business.dto.TranslatedDto;
import com.airbus.retex.model.mesureUnit.MeasureUnit;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MeasureUnitTranslatedDto extends TranslatedDto {
    private Long id;

    private TranslateDto name = new TranslateDto(this, MeasureUnitsFieldsEnum.name);

    @Override
    public String getClassName() {
        return MeasureUnit.class.getSimpleName();
    }

    @Override
    public Long getEntityId() {
        return id;
    }
}
