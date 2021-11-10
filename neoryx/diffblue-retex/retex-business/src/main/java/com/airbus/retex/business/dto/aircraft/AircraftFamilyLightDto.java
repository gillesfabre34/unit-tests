package com.airbus.retex.business.dto.aircraft;

import com.airbus.retex.business.dto.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AircraftFamilyLightDto implements Dto{
    private Long id;

    private String name;

}
