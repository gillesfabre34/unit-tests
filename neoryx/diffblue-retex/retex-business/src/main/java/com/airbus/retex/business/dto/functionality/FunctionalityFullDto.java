package com.airbus.retex.business.dto.functionality;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.model.common.Language;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class FunctionalityFullDto implements Dto {
    private Long id;
    private Map<Language, Map<FunctionalityFieldsEnum, String>> translatedFields;
}
