package com.airbus.retex.business.dto.damage.functionality;

import com.airbus.retex.business.dto.functionality.FunctionalityFullDto;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.functionality.damage.FunctionalityDamageFieldsEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.Map;

@Getter
@Setter
public class FunctionalityDamageDto extends FunctionalityDamageLightDto {

    @ApiModelProperty(value = "Map<Language, Map<FunctionalityDamageFieldsEnum, String>> : List of translated parameters to save")
    private Map<Language, Map<FunctionalityDamageFieldsEnum, String>> translatedFields;

    @Nullable
    private FunctionalityFullDto functionality;
}
