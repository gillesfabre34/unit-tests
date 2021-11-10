package com.airbus.retex.business.dto.user;

import com.airbus.retex.business.dto.CreationDto;
import com.airbus.retex.business.dto.feature.FeatureRightDto;
import com.airbus.retex.model.common.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateDto implements CreationDto {

    @NotNull
    private Language language;

    @NotNull
    private List<Long> roleIds;

    private List<FeatureRightDto> features;
}
