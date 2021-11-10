package com.airbus.retex.business.dto.functionalAreaName;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.business.dto.TranslatedDto;
import com.airbus.retex.model.functional.FunctionalAreaName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FunctionalAreaNameDto extends TranslatedDto {

    private Long id;

    private final TranslateDto name = new TranslateDto(this, FunctionalAreaNameFieldsEnum.name );

    @Override
    public String getClassName() {
        return FunctionalAreaName.class.getSimpleName();
    }

    @Override
    public Long getEntityId() {
        return id;
    }

}

