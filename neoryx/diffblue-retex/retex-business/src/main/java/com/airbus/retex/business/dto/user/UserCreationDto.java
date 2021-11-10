package com.airbus.retex.business.dto.user;

import com.airbus.retex.business.dto.CreationDto;
import com.airbus.retex.business.dto.feature.FeatureRightDto;
import com.airbus.retex.model.common.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCreationDto implements CreationDto {

    @NotNull
    @NotEmpty
    private String email;

    @NotNull
    private Long airbusEntityId;

    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    @NotNull
    @NotEmpty
    private String staffNumber;

    @NotNull
    private Language language;

    @NotNull
    private List<Long> roleIds;

    private List<FeatureRightDto> features;

    private Boolean isOperator;

    private Boolean isTechnicalResponsible;
}
