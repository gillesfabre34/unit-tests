package com.airbus.retex.business.dto.origin;

import com.airbus.retex.business.dto.Dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OriginLightDto implements Dto {
    private Long id;
    private String name;
}
