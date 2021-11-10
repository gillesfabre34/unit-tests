package com.airbus.retex.business.dto.step;

import com.airbus.retex.business.dto.media.MediaDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class StepDto extends StepLightDto {
    private Set<MediaDto> files;
}
