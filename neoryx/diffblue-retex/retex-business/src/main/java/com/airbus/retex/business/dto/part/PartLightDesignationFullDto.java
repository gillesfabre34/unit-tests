package com.airbus.retex.business.dto.part;

import com.airbus.retex.business.dto.IVersionableOutDto;
import com.airbus.retex.business.dto.media.MediaDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartLightDesignationFullDto implements IVersionableOutDto {
    private Long id;
    private String partNumber;
    private String partNumberRoot;
    private PartDesignationFullDto partDesignation;
    private MediaDto media;

    private Boolean isLatestVersion;
    private EnumStatus status;

    @Override
    public void setNaturalId(Long naturalId) {
        this.id = naturalId;
    }
}
