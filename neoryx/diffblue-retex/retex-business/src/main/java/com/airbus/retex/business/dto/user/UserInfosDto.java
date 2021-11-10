package com.airbus.retex.business.dto.user;

import com.airbus.retex.business.dto.feature.FeatureRightDto;
import com.airbus.retex.business.dto.role.RoleDto;
import com.airbus.retex.model.common.Language;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserInfosDto extends UserLightDto {

    private String email;

    private String staffNumber;

    private Language language;

    private String state;

    private List<FeatureRightDto> features;

    private List<RoleDto> roles;
}
