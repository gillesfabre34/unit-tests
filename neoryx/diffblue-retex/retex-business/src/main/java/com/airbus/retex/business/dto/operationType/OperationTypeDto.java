package com.airbus.retex.business.dto.operationType;

import com.airbus.retex.model.operation.OperationTypeBehaviorEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationTypeDto extends OperationTypeLightDto {
    private String template;
    private OperationTypeBehaviorEnum behavior;
}
