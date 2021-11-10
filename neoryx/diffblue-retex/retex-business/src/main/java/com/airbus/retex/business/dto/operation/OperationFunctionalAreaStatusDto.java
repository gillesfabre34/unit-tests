package com.airbus.retex.business.dto.operation;

import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperationFunctionalAreaStatusDto {

    private Long id;
    private EnumStatus status;

}
