package com.airbus.retex.business.dto.feature;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.enumeration.EnumRightLevelDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleFeatureDto implements Dto {
    private Long id;
    private EnumRightLevelDto rightLevel;
    private FeatureDto feature;
}
