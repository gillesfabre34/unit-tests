package com.airbus.retex.business.dto.treatment;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.business.dto.TranslatedDto;
import com.airbus.retex.model.treatment.Treatment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TreatmentDto extends TranslatedDto {

    private Long id;

    private final TranslateDto name = new TranslateDto(this, TreatmentFieldsEnum.name);

    @Override
    public String getClassName() {
        return Treatment.class.getSimpleName();
    }

    @Override
    public Long getEntityId() {
        return id;
    }
}
