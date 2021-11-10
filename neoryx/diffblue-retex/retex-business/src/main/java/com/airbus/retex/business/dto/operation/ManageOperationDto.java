package com.airbus.retex.business.dto.operation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
@Setter
public class ManageOperationDto {

    @Nullable
    private List<OperationDto> operations;

    @Nullable
    private List<OperationCreationDto> addedOperations;

    @Nullable
    private List<Long> deletedOperations;

}
