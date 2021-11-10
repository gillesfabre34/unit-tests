package com.airbus.retex.business.dto.airbusentity;

import com.airbus.retex.business.dto.CreationDto;
import com.airbus.retex.business.dto.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirbusEntityLightDto implements Dto, CreationDto {
    private Long id;
    private String code;
    private String countryName;
}
