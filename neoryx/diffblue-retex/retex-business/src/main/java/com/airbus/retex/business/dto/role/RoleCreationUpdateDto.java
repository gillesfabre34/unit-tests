package com.airbus.retex.business.dto.role;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.LightDto;
import com.airbus.retex.business.dto.feature.FeatureRightDto;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.common.Language;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class RoleCreationUpdateDto implements Dto {

    private LightDto airbusEntity;

    private RoleCode roleCode;

    @Valid
    @NotEmpty
    private List<FeatureRightDto> features;

    private Map<Language, Map<RoleFieldEnum, String>> translatedFields;
}
