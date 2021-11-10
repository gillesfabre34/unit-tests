package com.airbus.retex.service.operation;

import com.airbus.retex.business.dto.operation.ManageOperationDto;
import com.airbus.retex.business.dto.operation.OperationCreationDto;
import com.airbus.retex.business.dto.operation.OperationDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.routing.Routing;

import java.util.List;
import java.util.Set;

public interface IOperationService {
    /**
     * Delete list Operation
     */
    void deleteOperationsByIds(Routing routing, List<Long> listIdDeletedOperations) throws FunctionalException;

    void addOperations(Routing routing, List<OperationCreationDto> listOperations) throws NotFoundException;

    void updateOperations(Routing routing, List<OperationDto> listOperations);

    void checkOrderNumber(ManageOperationDto manageOperationDto) throws FunctionalException;

    void cleanOrderNumber(Set<Operation> listOperations);
}
