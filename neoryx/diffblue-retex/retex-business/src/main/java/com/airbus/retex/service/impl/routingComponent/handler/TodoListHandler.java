package com.airbus.retex.service.impl.routingComponent.handler;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.dto.routingComponent.RoutingComponentCreateUpdateDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFullDto;
import com.airbus.retex.business.dto.step.StepCreationDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.operation.OperationTypeBehaviorEnum;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.task.TodoListName;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.persistence.todoList.TodoListNameRepository;
import com.airbus.retex.service.routingComponent.IRoutingComponentHandlerService;
import com.airbus.retex.service.routingComponent.IRoutingComponentService;

@Service
@Transactional(rollbackFor = Exception.class)
public class TodoListHandler extends AbstractHandler implements IRoutingComponentHandlerService {

    @Autowired
    private TodoListNameRepository todoListNameRepository;
    @Autowired
    private IRoutingComponentService routingComponentService;

    /**
     * @inheritDoc
     */
    @Override
    public RoutingComponentFullDto handle(RoutingComponentCreateUpdateDto todoLisCreationDto,
                                          Boolean validated,
                                          @Nullable RoutingComponentIndex existingRoutingComponentIndex) throws FunctionalException {

        todoLisCreationDto.setStatus(validated ? EnumStatus.VALIDATED : EnumStatus.CREATED);

        // by default create a an empty step, without posts
        // we will use just to associated different additional information of the component
        List<StepCreationDto> steps = todoLisCreationDto.getSteps();
        if (steps != null && steps.size() != 1) {
            throw new FunctionalException("retex.rc.generic.nb.steps.not.accepted");
        }

        if (null == steps || CollectionUtils.isEmpty(steps)) {
            throw new FunctionalException("retex.routing.component.steps.list.empty");
        }

        RoutingComponentIndex routingComponentIndex = null;
        if (null != existingRoutingComponentIndex) { //UPDATE
            routingComponentIndex = updateVersion(existingRoutingComponentIndex.getNaturalId(),
                    routingComponentIndex1 -> mapDtoToVersion(todoLisCreationDto, routingComponentIndex1));
        } else { //Creation nouveau
            routingComponentIndex = new RoutingComponentIndex();
            routingComponentIndex.setTodoList(new TodoList());
            mapDtoToVersion(todoLisCreationDto, routingComponentIndex);
            routingComponentIndex = createVersion(routingComponentIndex);
        }

        return routingComponentService.buildRoutingComponentDto(routingComponentIndex, routingComponentIndex.getTodoList(), routingComponentIndex.getTodoList().getInspection().getValue(),
                routingComponentIndex.getTodoList().getTodoListNameId(), null, routingComponentIndex.getNaturalId());
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean supports(OperationType operationType) {
        return operationType.isBehavior(OperationTypeBehaviorEnum.TODO_LIST);
    }

    /**
     * @param todoLisCreationDto
     * @param todoList
     * @return
     * @throws FunctionalException
     */
    private Optional<TodoListName> addTodoListName(RoutingComponentCreateUpdateDto todoLisCreationDto, TodoList todoList) throws FunctionalException {
        TodoListName todoListName = todoListNameRepository.findById(todoLisCreationDto.getTaskId()).orElseThrow(() -> new FunctionalException("retex.routing.component.todo.list.notExists"));
        todoList.setTodoListName(todoListName);
        todoList.setTodoListNameId(todoListName.getId());
        return Optional.of(todoListName);
    }

    private void prepareTodoListForUpdateOrCreate(TodoList todoList, RoutingComponentCreateUpdateDto todoLisCreationDto) throws FunctionalException{
        addOperationType(todoLisCreationDto, todoList);
        addTodoListName(todoLisCreationDto, todoList);
        addInspection(todoLisCreationDto, todoList);
    }

    @Override
    protected void mapDtoToVersion(RoutingComponentCreateUpdateDto updateDto, RoutingComponentIndex version) throws FunctionalException{
        version.setStatus(updateDto.getStatus());
        prepareTodoListForUpdateOrCreate(version.getTodoList(), updateDto);
        associateEmptyStepAndAdditionalInformations(updateDto.getSteps().get(0), version);
    }

}
