package com.airbus.retex.business.dto.part;

import com.airbus.retex.business.dto.VersionableOutDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartLightDto extends VersionableOutDto {

    private String partNumber;

    private String partNumberRoot;

    private PartDesignationDto partDesignation;

}
