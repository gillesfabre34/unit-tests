package com.airbus.retex.business.dto.environment;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.business.dto.TranslatedDto;
import com.airbus.retex.model.environment.Environment;
import com.airbus.retex.model.environment.EnvironmentFieldsEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EnvironmentLightDto extends TranslatedDto {

    private Long id;

    private final TranslateDto name = new TranslateDto(this, EnvironmentFieldsEnum.name);

    @Override
    public String getClassName() {
        return Environment.class.getSimpleName();
    }

    @Override
    public Long getEntityId() {
        return id;
    }

}
