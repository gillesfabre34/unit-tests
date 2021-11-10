package com.airbus.retex.business.dto.part;

import com.airbus.retex.business.dto.functionalArea.FunctionalAreaFullDto;
import com.airbus.retex.business.dto.mpn.MpnDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PartFullDto extends PartDto {

    private List<MpnDto> mpnCodes;
    private List<FunctionalAreaFullDto> functionalAreas;

    private Boolean isLatestVersion;
    private Long versionNumber;
    private EnumStatus status;
    private LocalDateTime creationDate;
}
