package com.airbus.retex.business.dto.childRequest;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.user.IIdentifiableDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChildRequestLightDto implements Dto, IIdentifiableDto<Long> {
    private Long id;
    private String partNumberRoot;
    private String partNumber;
    private String designation;
    private Long drtTotal;
    private Long drtTreated;
    private Long versionNumber;
    private EnumStatus status;
    private Boolean isDeletable;
}
