package com.airbus.retex.business.dto.damage;

import com.airbus.retex.business.dto.VersionableOutDto;
import com.airbus.retex.business.dto.damage.functionality.FunctionalityDamageCreationDto;
import com.airbus.retex.business.dto.media.MediaDto;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class DamageDto extends VersionableOutDto {
    private Set<FunctionalityDamageCreationDto> functionalityDamages;
    private List<MediaDto> images;
}
