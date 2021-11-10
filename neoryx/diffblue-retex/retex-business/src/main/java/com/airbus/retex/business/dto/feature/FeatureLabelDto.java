package com.airbus.retex.business.dto.feature;

import com.airbus.retex.business.dto.CreationDto;
import com.airbus.retex.business.dto.Dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FeatureLabelDto implements Dto, CreationDto {
    private Long id;
    private String isoCode;
    private String value;
}
