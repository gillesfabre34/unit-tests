package com.airbus.retex.business.dto.functionality;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.business.dto.TranslatedDto;
import com.airbus.retex.model.functionality.Functionality;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FunctionalityLightDto extends TranslatedDto {

    private Long id;

    private final TranslateDto name = new TranslateDto(this, FunctionalityFieldsEnum.name);

    @Override
    public String getClassName() {
        return Functionality.class.getSimpleName();
    }

    @Override
    public Long getEntityId() {
        return id;
    }
}
