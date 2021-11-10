package com.airbus.retex.business.dto.functionality;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.model.common.Language;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FunctionalityCreationDto implements Dto {

    @ApiModelProperty(value = "Map<Language, Map<FunctionalityFieldsEnum, String>> : List of translated parameters to save")
    private Map<Language, Map<FunctionalityFieldsEnum, String>> translatedFields;
}
