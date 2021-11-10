package com.airbus.retex.business.dto.aircraft;

import com.airbus.retex.business.dto.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AircraftTypeLightDto implements Dto {
    private Long id;

    private String name;

}
