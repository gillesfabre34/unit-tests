package com.airbus.retex.business.dto.operation;

import com.airbus.retex.business.dto.VersionableInDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
@Setter
public class OperationCreationDto extends VersionableInDto {
    private Integer orderNumber;
    private Long operationTypeId;
    private Long routingId;

    @Nullable
    private List<Long> todoListId;

}
