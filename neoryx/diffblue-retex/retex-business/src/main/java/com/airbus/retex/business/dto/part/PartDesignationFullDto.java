package com.airbus.retex.business.dto.part;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.part.PartDesignationFieldsEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PartDesignationFullDto implements Dto {

    private Long id;
    private Map<Language, Map<PartDesignationFieldsEnum, String>> translatedFields;

}
