package com.airbus.retex.business.dto.feature;

import com.airbus.retex.model.common.EnumRightLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleFeatureLightDto {
    private EnumRightLevel rightLevel;
    private Long featureId;
}
