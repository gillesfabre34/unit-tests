package com.airbus.retex.business.dto.part;

import com.airbus.retex.business.dto.VersionableOutDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartNumberDto extends VersionableOutDto {

    private String partNumber;
}
