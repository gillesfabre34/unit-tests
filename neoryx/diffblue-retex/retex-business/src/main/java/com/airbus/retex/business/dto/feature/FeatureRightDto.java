package com.airbus.retex.business.dto.feature;

import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeatureRightDto {

    //TODO Use an enum dto ? (the question is : is it usefull to have a mirror dto enum for all model's enum ?)
    @NotNull
    private FeatureCode code;

    @NotNull
    private EnumRightLevel rightLevel;
}
