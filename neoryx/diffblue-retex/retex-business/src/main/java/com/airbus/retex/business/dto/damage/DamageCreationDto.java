package com.airbus.retex.business.dto.damage;

import com.airbus.retex.business.dto.CreationDto;
import com.airbus.retex.business.util.annotation.NameValidation;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.damage.DamageFieldsEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class DamageCreationDto implements CreationDto {

    @ApiModelProperty(value = "Map<Language, Map<DamageFieldsEnum, String>> : List of translated parameters to save")
    @NotNull
    @NameValidation(message = "{retex.regex.name.not.valid}")
    private Map<Language, Map<DamageFieldsEnum, String>> translatedFields;
}

