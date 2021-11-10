package com.airbus.retex.business.dto.feature;

import com.airbus.retex.business.dto.enumeration.EnumRightLevelDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FeatureRoleUpdateDto {
    private Long id;
    private EnumRightLevelDto rightLevel;
    private Long featureId;
}
