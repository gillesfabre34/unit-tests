package com.airbus.retex.service.operationType;

import com.airbus.retex.business.dto.operationType.OperationTypeDto;

import java.util.List;

public interface IOperationTypeService {


    /**
     * get Operations types
     */
    List<OperationTypeDto> findOperationTypes();
}
