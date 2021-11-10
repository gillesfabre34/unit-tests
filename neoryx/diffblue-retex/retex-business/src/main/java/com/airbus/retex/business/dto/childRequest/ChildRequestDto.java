package com.airbus.retex.business.dto.childRequest;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.user.IIdentifiableDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChildRequestDto implements Dto, IIdentifiableDto<Long> {
    private Long id;
    private Long versionNumber;
    private EnumStatus status;

    @Override
    public Long getId() {
        return id;
    }
}
