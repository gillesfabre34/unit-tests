package com.airbus.retex.business.dto.functionalArea;

import com.airbus.retex.business.dto.VersionableOutDto;
import com.airbus.retex.business.dto.functionalAreaName.FunctionalAreaNameDto;
import com.airbus.retex.business.dto.functionality.FunctionalityLightDto;
import com.airbus.retex.business.dto.treatment.TreatmentDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FunctionalAreaHeaderDto extends VersionableOutDto {
    private Long areaNumber;

    private FunctionalityLightDto functionality;

    private String classification;

    private String material;

    private TreatmentDto treatment;

    private FunctionalAreaNameDto functionalAreaName;
}
