package com.airbus.retex.business.dto.routing;

import java.time.LocalDateTime;

import com.airbus.retex.business.dto.part.PartLightDto;
import com.airbus.retex.model.common.EnumStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoutingDto extends RoutingLightDto {

    private LocalDateTime creationDate;
    private EnumStatus status;
    private Long versionNumber;
    private Boolean isLatestVersion;
    private PartLightDto part;
    private boolean deletable;

}
