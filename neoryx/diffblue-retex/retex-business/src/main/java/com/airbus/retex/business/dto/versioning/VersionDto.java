package com.airbus.retex.business.dto.versioning;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VersionDto implements Dto {

    private LocalDateTime dateUpdate;
    private Long versionNumber;
    private EnumStatus status;
    private Boolean isLatestVersion;

}
