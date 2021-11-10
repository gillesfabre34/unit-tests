package com.airbus.retex.business.dto.damage.functionality;

import com.airbus.retex.business.dto.VersionableOutDto;
import com.airbus.retex.business.dto.media.MediaDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FunctionalityDamageLightDto extends VersionableOutDto {

    private MediaDto image;

}
