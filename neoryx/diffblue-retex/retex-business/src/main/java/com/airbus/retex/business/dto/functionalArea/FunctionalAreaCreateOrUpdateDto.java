package com.airbus.retex.business.dto.functionalArea;

import com.airbus.retex.business.dto.VersionableInDto;
import com.airbus.retex.model.classification.EnumClassification;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class FunctionalAreaCreateOrUpdateDto extends VersionableInDto {

    @NotBlank
    @Length(max = 2)
    private String areaNumber;

    @NotNull
    private Long faNameId;

    private boolean disabled;

    @NotNull
    private Long functionalityId;

    private EnumClassification classification;

    private String material;

    private Long treatmentId;

}


