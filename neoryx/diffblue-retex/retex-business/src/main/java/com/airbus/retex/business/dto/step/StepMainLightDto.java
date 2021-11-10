package com.airbus.retex.business.dto.step;

import com.airbus.retex.business.dto.IVersionableOutDto;
import com.airbus.retex.business.dto.TranslatedDto;
import com.airbus.retex.model.step.Step;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StepMainLightDto extends TranslatedDto implements IVersionableOutDto {

    private Long naturalId;

    @JsonIgnore
    private Long technicalId;

    @Override
    public String getClassName() {
        return Step.class.getSimpleName();
    }

    @Override
    public Long getEntityId() {
        return technicalId;
    }

    @Override
    public Long getId() {
        return this.naturalId;
    }
}
