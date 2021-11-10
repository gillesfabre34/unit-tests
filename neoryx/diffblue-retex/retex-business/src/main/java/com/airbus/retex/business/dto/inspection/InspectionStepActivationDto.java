package com.airbus.retex.business.dto.inspection;

import java.util.List;

import com.airbus.retex.business.dto.VersionableInDto;
import com.airbus.retex.business.dto.drtPicture.DrtPicturesDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InspectionStepActivationDto extends VersionableInDto {
    private List<InspectionPostValueDto> posts;

    private DrtPicturesDto drtPictures;

    private Long idRoutingComponentIndex;
}
