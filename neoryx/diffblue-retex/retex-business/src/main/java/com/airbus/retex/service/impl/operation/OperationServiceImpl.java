package com.airbus.retex.service.impl.operation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.dto.operation.ManageOperationDto;
import com.airbus.retex.business.dto.operation.OperationCreationDto;
import com.airbus.retex.business.dto.operation.OperationDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.model.basic.AncestorContext;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.operation.OperationTypeBehaviorEnum;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.service.impl.operation.mapper.OperationMapper;
import com.airbus.retex.service.operation.IOperationService;
import com.airbus.retex.service.step.IStepActivationService;

@Service
@Transactional(rollbackFor = Exception.class)
public class OperationServiceImpl implements IOperationService {


    @Autowired
    private IStepActivationService stepActivationService;


    @Autowired
    private OperationMapper operationMapper;

    @Override
    public void deleteOperationsByIds(Routing routing, List<Long> listIdDeletedOperation) {

        Map<Long, Operation> operationMap = routing.getOperations()
                                                    .stream()
                                                    .collect(Collectors.toMap(Operation::getNaturalId, ope -> ope));

        listIdDeletedOperation.forEach(id -> {
            if (operationMap.containsKey(id)) {
                routing.getOperations().remove(operationMap.get(id));
            }
        });
    }

    @Override
    public void addOperations(Routing routing, List<OperationCreationDto> listOperationsDto) throws NotFoundException {
        for(OperationCreationDto operationDto : listOperationsDto) {
            Operation operation = new Operation();
            operationMapper.updateOperation(operationDto, operation, new AncestorContext());

            OperationType operationType = operation.getOperationType();

            if (null == operationType) {
                continue;
            }

            if (operationType.isBehavior(OperationTypeBehaviorEnum.VISUAL_COMPONENT) ||
                        operationType.isBehavior(OperationTypeBehaviorEnum.ROUTING_COMPONENT)) {

                for (FunctionalArea functionalArea : routing.getPart().getFunctionalAreas()) {
                    OperationFunctionalArea operationFunctionalArea = new OperationFunctionalArea();
                    operationFunctionalArea.setFunctionalArea(functionalArea);
                    operation.addOperationFunctionalArea(operationFunctionalArea);

                    stepActivationService.createAllStepActivations(operationFunctionalArea, operation.getOperationType().getId());
                }

            }

            routing.addOperation(operation);
        }
    }

    @Override
    public void updateOperations(Routing routing, List<OperationDto> listOperationsDto) {
        operationMapper.updateListOperationNumber(
            routing,
            listOperationsDto,
            routing.getOperations()
        );
    }

    @Override
    public void checkOrderNumber(ManageOperationDto manageOperationDto) throws FunctionalException {
        List<Operation> operationList = new ArrayList<>();
        Integer lastOrderNumber = 0;

        if(null != manageOperationDto.getOperations()) {
            List<Operation> operations = operationMapper.updateListOperation(
                    manageOperationDto.getOperations(),
                    new AncestorContext());
            operationList.addAll(operations);
        }

        if(null != manageOperationDto.getAddedOperations()) {
            List<Operation> addOperations = operationMapper.updateListCreateOperation(manageOperationDto.getAddedOperations());
            operationList.addAll(addOperations);
        }
        operationList.sort(Comparator.comparing(Operation::getOrderNumber));

        for (Operation operation: operationList) {
            if (operation.getOrderNumber().equals(lastOrderNumber)) {
                throw new FunctionalException("retex.error.routing.operation.order.number.exist");
            }
            lastOrderNumber = operation.getOrderNumber();
        }
    }

    @Override
    public void cleanOrderNumber(Set<Operation> listOperations) {
        Integer lastOrderNumber = 0;

        List<Operation> operationSorted = new ArrayList<>(listOperations);
        operationSorted.sort(Comparator.comparing(Operation::getOrderNumber));

        for (Operation operation : operationSorted) {
            if (operation.getOrderNumber() > (lastOrderNumber + 1)) { // Si il y a un écart entre deux order_Number qui se suivent
                operation.setOrderNumber(lastOrderNumber + 1);
            }
            lastOrderNumber = operation.getOrderNumber();
        }
    }

}
