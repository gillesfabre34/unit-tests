package com.airbus.retex.business.dto.operationPost;

import com.airbus.retex.business.dto.VersionableOutDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoutingFunctionalAreaPostLightDto extends VersionableOutDto {
    private Float threshold;
}
