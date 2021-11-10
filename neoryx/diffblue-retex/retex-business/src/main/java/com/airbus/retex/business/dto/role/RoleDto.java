package com.airbus.retex.business.dto.role;

import com.airbus.retex.business.dto.airbusentity.AirbusEntityLightDto;
import com.airbus.retex.business.dto.feature.FeatureRightDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleDto extends RoleLightDto {

    private AirbusEntityLightDto airbusEntity;

    private List<FeatureRightDto> features;
}
