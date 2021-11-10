package com.airbus.retex.business.dto.step;

import com.airbus.retex.business.dto.VersionableInDto;
import com.airbus.retex.business.dto.operationPost.RoutingFunctionalAreaPostDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StepUpdateDto extends VersionableInDto {

    private Boolean activated;

    private List<RoutingFunctionalAreaPostDto> posts;
}
