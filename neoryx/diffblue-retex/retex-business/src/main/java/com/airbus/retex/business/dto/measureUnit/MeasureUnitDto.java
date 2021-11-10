package com.airbus.retex.business.dto.measureUnit;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.model.common.Language;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
public class MeasureUnitDto implements Dto {
    private Long id;
    private Map<Language, Map<MeasureUnitsFieldsEnum, String>> translatedFields;
}
