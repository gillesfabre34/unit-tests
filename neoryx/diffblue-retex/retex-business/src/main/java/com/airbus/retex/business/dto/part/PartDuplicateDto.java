package com.airbus.retex.business.dto.part;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.ata.AtaLightDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartDuplicateDto implements Dto {

    private String partNumberRoot;

    private AtaLightDto ata;

    private PartDesignationDto partDesignation;
}
