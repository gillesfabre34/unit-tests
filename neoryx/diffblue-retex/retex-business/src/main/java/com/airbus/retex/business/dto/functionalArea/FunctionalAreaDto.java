package com.airbus.retex.business.dto.functionalArea;

import com.airbus.retex.business.dto.VersionableOutDto;
import com.airbus.retex.business.dto.functionalAreaName.FunctionalAreaNameDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FunctionalAreaDto extends VersionableOutDto { //TODO USE VERSIONABLEOUTDTO (When Routing versioning is merged)

    private String areaNumber;

    private FunctionalAreaNameDto functionalAreaName;

}
