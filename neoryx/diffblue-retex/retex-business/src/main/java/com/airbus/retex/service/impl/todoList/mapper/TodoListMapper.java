package com.airbus.retex.service.impl.todoList.mapper;

import com.airbus.retex.business.dto.inspection.InspectionDetailsDto;
import com.airbus.retex.business.dto.inspection.RoutingInspectionDetailsDto;
import com.airbus.retex.business.dto.step.StepCustomDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.mapper.AbstractMapper;
import com.airbus.retex.business.mapper.ReferenceMapper;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.todoList.TodoList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ReferenceMapper.class})
public abstract class TodoListMapper extends AbstractMapper {

    /* -----MAPPING TodoList ----- */
    @Mapping(source = "routingComponentIndex.naturalId", target = "idRoutingComponentIndex")
    @Mapping(source = "routingComponentIndex.isLatestVersion", target = "isLatestVersion")
    @Mapping(source = "routingComponentIndex.status", target = "status")
    public abstract RoutingInspectionDetailsDto createRoutingInspectionDetailsDto(TodoList source);

    /* -----MAPPING TodoList ----- */
    @Mapping(source = "routingComponentIndex.naturalId", target = "idRoutingComponentIndex")
    @Mapping(source = "routingComponentIndex.versionNumber", target = "version")
    @Mapping(source = "operationType.behavior", target = "behavior")
    public abstract InspectionDetailsDto createInspectionDetailsDto(TodoList source);

    /* -----MAPPING TodoList ----- */
    public InspectionDetailsDto createInspectionDetailsDto(Operation operation, Long taskId) throws FunctionalException {
        return createInspectionDetailsDto(getTodoListFromOperationAndTaskId(operation, taskId));
    }

    public abstract List<StepCustomDto> updateStep(Collection<Step> source);


    /**
     * Returns todoList by operation and todolist id
     * @param operation
     * @param taskId
     * @return
     * @throws FunctionalException
     */
    public TodoList getTodoListFromOperationAndTaskId(Operation operation, Long taskId) throws FunctionalException {
        List<TodoList> todoListList = operation.getTodoLists().stream()
                .filter(todoList -> todoList.getNaturalId().equals(taskId))
                .collect(Collectors.toList());
        if (todoListList.size() != 1) {
            throw new FunctionalException("retex.error.todolist.not.unique");
        }
        return todoListList.get(0);
    }


}
