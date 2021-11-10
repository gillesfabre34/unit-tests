package com.airbus.retex.service.routingComponentIndex;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentIndexDto;
import com.airbus.retex.model.common.StepType;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.persistence.routingComponent.RoutingComponentIndexRepository;
import com.airbus.retex.persistence.routingComponent.RoutingComponentRepository;
import com.airbus.retex.service.impl.routingComponent.mapper.RoutingComponentIndexServiceDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetRoutingComponentIndexIT extends AbstractServiceIT {

    @Autowired
    private RoutingComponentIndexServiceDtoMapper routingComponentIndexServiceDtoMapper;
    @Autowired
    private RoutingComponentRepository routingComponentRepository;
    @Autowired
    private RoutingComponentIndexRepository routingComponentIndexRepository;

    private Page<RoutingComponentIndex> pageItemRoutingComponentsIndex;
    private RoutingComponentIndex routingComponentIndex;
    private RoutingComponent routingComponent;
    private TodoList todoList;
    private Step stepOne;
    private List<RoutingComponentIndexDto> routingComponentIndexDtos = new ArrayList<>();

    @BeforeEach
    public void before() {
        runInTransaction(() -> {
            routingComponent = dataSetInitializer.createRoutingComponent();
            stepOne = dataSetInitializer.createStep(1, routingComponent, StepType.AUTO, Set.of());
            routingComponent.setSteps(List.of(stepOne));

            todoList = dataSetInitializer.createTodoList();
        });
    }

    @Test
    public void getAllRCWithDeletableTrue_ok() {
        routingComponentIndex = dataSetInitializer.createRoutingComponentIndex(routingComponent.getTechnicalId(), null);
        routingComponentIndex = routingComponentIndexRepository.findLastVersionByNaturalId(routingComponentIndex.getNaturalId()).get();
        pageItemRoutingComponentsIndex = new PageImpl<>(List.of(routingComponentIndex));
        routingComponentIndexDtos = routingComponentIndexServiceDtoMapper.pageRoutingComponentIndexToDto(pageItemRoutingComponentsIndex);

        assertTrue(routingComponentIndexDtos.get(0).isDeletable());
    }

    @Test
    public void getAllRCWithDeletableFalse_ok() {
        routingComponentIndex = dataSetInitializer.createRoutingComponentIndex(routingComponent.getTechnicalId(), null);
        routingComponentIndex = routingComponentIndexRepository.findLastVersionByNaturalId(routingComponentIndex.getNaturalId()).get();
        dataSetInitializer.createStepActivation(true, stepOne, dataset.operationFunctionalArea_one);
        pageItemRoutingComponentsIndex = new PageImpl<>(List.of(routingComponentIndex));
        routingComponentIndexDtos = routingComponentIndexServiceDtoMapper.pageRoutingComponentIndexToDto(pageItemRoutingComponentsIndex);

        assertFalse(routingComponentIndexDtos.get(0).isDeletable());
    }

    @Test
    public void getAllTodoListWithDeletableTrue_ok() {
        routingComponentIndex = dataSetInitializer.createRoutingComponentIndex(null, todoList.getTechnicalId());
        routingComponentIndex = routingComponentIndexRepository.findLastVersionByNaturalId(routingComponentIndex.getNaturalId()).get();
        pageItemRoutingComponentsIndex = new PageImpl<>(List.of(routingComponentIndex));
        routingComponentIndexServiceDtoMapper.pageRoutingComponentIndexToDto(pageItemRoutingComponentsIndex);
        routingComponentIndexDtos = routingComponentIndexServiceDtoMapper.pageRoutingComponentIndexToDto(pageItemRoutingComponentsIndex);

        assertTrue(routingComponentIndexDtos.get(0).isDeletable());
    }

    @Test
    public void getAllTodoListWithDeletableFalse_ok() {
        runInTransaction(() -> {
            routingComponentIndex = dataSetInitializer.createRoutingComponentIndex(null, todoList.getTechnicalId());

            dataSetInitializer.createOperation(1, (ope) -> {
                ope.setOperationType(dataset.operationType_todolist);
                ope.addTodoList(routingComponentIndex.getTodoList());
            });
        });

        routingComponentIndex = routingComponentIndexRepository.findLastVersionByNaturalId(routingComponentIndex.getNaturalId()).get();

        pageItemRoutingComponentsIndex = new PageImpl<>(List.of(routingComponentIndex));
        routingComponentIndexDtos = routingComponentIndexServiceDtoMapper.pageRoutingComponentIndexToDto(pageItemRoutingComponentsIndex);

        assertFalse(routingComponentIndexDtos.get(0).isDeletable());
    }
}
