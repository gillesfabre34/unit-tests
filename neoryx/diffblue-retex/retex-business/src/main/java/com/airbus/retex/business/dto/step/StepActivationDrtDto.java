package com.airbus.retex.business.dto.step;

import com.airbus.retex.business.dto.VersionableOutDto;
import com.airbus.retex.business.dto.drtPicture.DrtPicturesDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StepActivationDrtDto extends VersionableOutDto {
    private boolean activated;

    private DrtPicturesDto drtPictures;
}
