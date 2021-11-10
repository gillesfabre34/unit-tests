package com.airbus.retex.business.dto.feature;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.model.common.EnumRightLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LightFeatureLabelDto implements Dto {

    private Long id;

    private EnumRightLevel rightLevel;

    private String label;
}
