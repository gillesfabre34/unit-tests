package com.airbus.retex.business.dto.step;

import com.airbus.retex.business.dto.ITranslationDto;
import com.airbus.retex.business.dto.VersionableOutDto;
import com.airbus.retex.model.common.StepType;
import com.airbus.retex.model.step.StepFieldsEnum;
import com.airbus.retex.model.step.StepTranslation;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StepLightDto extends VersionableOutDto implements ITranslationDto<StepTranslation, StepFieldsEnum> {

    @JsonIgnore
    private Long technicalId;

    private int stepNumber;
    private StepType type;

    @Override
    public Class<StepTranslation> getClassTranslation() {
        return StepTranslation.class;
    }

    @Override
    public Long getEntityId() {
        return technicalId;
    }
}
