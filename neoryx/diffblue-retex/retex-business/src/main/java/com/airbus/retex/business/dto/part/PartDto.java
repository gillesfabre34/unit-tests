package com.airbus.retex.business.dto.part;

import com.airbus.retex.business.dto.ata.AtaLightDto;
import com.airbus.retex.business.dto.media.MediaDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartDto extends PartLightDto {

    private AtaLightDto ata;

    private EnumStatus status;

    private MediaDto image;

    private boolean deletable;

    private Long versionNumber;

}
