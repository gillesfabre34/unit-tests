package com.airbus.retex.business.dto.aircraft;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.user.IIdentifiableDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AircraftVersionLightDto implements Dto, IIdentifiableDto<Long> {
    private Long id;

    private String name;

}
