package com.airbus.retex.business.dto.operation;

import com.airbus.retex.business.dto.IVersionableOutDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperationLightDto implements IVersionableOutDto {

    private Long id;

    @Override
    public void setNaturalId(Long naturalId) {
        this.id = naturalId;
    }
}
