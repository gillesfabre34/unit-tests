package com.airbus.retex.service.impl.operation.mapper;

import static com.airbus.retex.business.mapper.MapperUtils.updateList;

import java.util.*;
import java.util.stream.Collectors;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.airbus.retex.business.dto.operation.OperationCreationDto;
import com.airbus.retex.business.dto.operation.OperationDto;
import com.airbus.retex.business.dto.operation.OperationFullDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.business.mapper.AbstractMapper;
import com.airbus.retex.business.mapper.ReferenceMapper;
import com.airbus.retex.model.basic.AncestorContext;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.persistence.routingComponent.RoutingComponentIndexRepository;


@Mapper(componentModel = "spring", uses = {ReferenceMapper.class})
public abstract class OperationMapper extends AbstractMapper {

    @Autowired
    private RoutingComponentIndexRepository routingComponentIndexRepository;

    @Mapping(source = "operationTypeId", target = "operationType")
    @Mapping(source = "todoListId", target = "todoLists")
    public abstract void updateOperation(OperationCreationDto operationDto, @MappingTarget Operation operation, @Context AncestorContext context);

    public abstract void updateOperation(OperationDto operationDto, @MappingTarget Operation operation, @Context AncestorContext context);

    public SortedSet<TodoList> map(List<Long> todoListId) throws FunctionalException {
        SortedSet<TodoList> todoLists = new TreeSet<>();

        if (null == todoListId) {
            return todoLists;
        }

        for (Long id : todoListId) {
            RoutingComponentIndex routingComponentIndex = routingComponentIndexRepository.findValidatedVersionByNaturalId(id)
                    .orElseThrow(() -> new FunctionalException("retex.error.routing.component.index.not.found"));
            if (null == routingComponentIndex.getTodoList()) {
                throw new NotFoundException("retex.error.todolist.not.found");
            }
            todoLists.add(routingComponentIndex.getTodoList());
        }

        return todoLists;
    }

    public List<Operation> updateListOperation(Collection<OperationDto> operationDtos, @Context AncestorContext context) {
        List<Operation> operations = new ArrayList<>();

        for (OperationDto dto : operationDtos ) {
            Operation operation = new Operation();
            updateOperation(dto, operation, context);
            operations.add(operation);
        }

        return operations;
    }

    public void updateListOperationNumber(Routing routing, Collection<OperationDto> operationDtos, Set<Operation> operations) {
        updateList(
            operationDtos,
            operations,
                (OperationDto source, Operation destination) -> source.getId().equals(destination.getNaturalId()),
            () -> {
                Operation operation = new Operation();
                operation.setRouting(routing);
                return operation;
            },
                (OperationDto source, Operation destination) -> destination.setOrderNumber(source.getOrderNumber())

        );
    }

    public List<Operation> updateListCreateOperation(List<OperationCreationDto> operationDtos) {
        List<Operation> operations = new ArrayList<>();

        updateList(
            operationDtos,
            operations,
            (OperationCreationDto source, Operation destination) -> {
                if (null == source.getNaturalId()) {
                    return false;
                }

                return source.getNaturalId().equals(destination.getNaturalId());
            },
            Operation::new,
                (OperationCreationDto source, Operation destination) -> destination
                        .setOrderNumber(source.getOrderNumber())

        );

        return operations;
    }

    @Mapping(source = "todoLists", target = "operationTodoLists")
    public abstract void toFullDto(Operation operation, @MappingTarget OperationFullDto operationDto, @Context AncestorContext context);

    public void toListFullDto(Collection<Operation> operations, @MappingTarget List<OperationFullDto> operationDtos, @Context AncestorContext context) {
        operationDtos.clear();
        operations = operations.stream().sorted(Comparator.comparing(Operation::getOrderNumber)).collect(Collectors.toList());
        operations.forEach(operation -> {
            OperationFullDto dto = new OperationFullDto();
            toFullDto(operation, dto, context);
            operationDtos.add(dto);
        });
    }

}
