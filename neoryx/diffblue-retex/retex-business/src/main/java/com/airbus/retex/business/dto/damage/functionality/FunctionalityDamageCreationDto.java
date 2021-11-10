package com.airbus.retex.business.dto.damage.functionality;

import com.airbus.retex.business.dto.CreationDto;
import com.airbus.retex.business.dto.functionality.FunctionalityDto;
import com.airbus.retex.business.dto.media.MediaDto;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.functionality.damage.FunctionalityDamageFieldsEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class FunctionalityDamageCreationDto implements CreationDto {
    private Long id;

    @ApiModelProperty(value = "Map<Language, Map<FunctionalityDamageFieldsEnum, String>> : List of translated parameters to save")
    private Map<Language, Map<FunctionalityDamageFieldsEnum, String>> translatedFields;

    @Nullable
    private MediaDto image;

    private FunctionalityDto functionality;
}
