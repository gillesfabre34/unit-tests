package com.airbus.retex.business.dto.user;

import com.airbus.retex.business.dto.feature.FeatureRightDto;
import com.airbus.retex.business.dto.role.RoleLightDto;
import com.airbus.retex.model.common.Language;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDto extends UserLightDto {

    private String email;

    private String staffNumber;

    private Language language;

    private String state;

    private List<RoleLightDto> roles;

    private List<FeatureRightDto> features;
}
