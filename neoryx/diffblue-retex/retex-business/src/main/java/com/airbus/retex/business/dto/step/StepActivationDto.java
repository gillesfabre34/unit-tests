package com.airbus.retex.business.dto.step;

import com.airbus.retex.business.dto.VersionableOutDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StepActivationDto extends VersionableOutDto {
    private boolean activated;

}
