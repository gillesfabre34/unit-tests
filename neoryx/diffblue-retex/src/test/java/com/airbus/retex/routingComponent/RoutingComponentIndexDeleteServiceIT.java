package com.airbus.retex.routingComponent;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.dataset.DatasetInitializer;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.persistence.routingComponent.RoutingComponentIndexRepository;
import com.airbus.retex.persistence.routingComponent.RoutingComponentRepository;
import com.airbus.retex.persistence.todoList.TodoListRepository;
import com.airbus.retex.service.routingComponent.IRoutingComponentIndexService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RoutingComponentIndexDeleteServiceIT extends AbstractServiceIT {

    @Autowired
    private IRoutingComponentIndexService routingComponentIndexService;
    @Autowired
    private DatasetInitializer datasetInitializer;
    @Autowired
    private RoutingComponentIndexRepository routingComponentIndexRepository;
    @Autowired
    private RoutingComponentRepository routingComponentRepository;
    @Autowired
    private TodoListRepository todoListRepository;

    private RoutingComponent routingComponent;
    private TodoList todoList;

    private RoutingComponentIndex routingComponentIndexRC;
    private RoutingComponentIndex routingComponentIndexTodoList;

    @BeforeEach
    public void before() {
        runInTransaction(() -> {
            routingComponent = datasetInitializer.createRoutingComponent();
            todoList = datasetInitializer.createTodoList();

            routingComponentIndexRC = datasetInitializer.createRoutingComponentIndex(routingComponent.getTechnicalId(), null, rci -> rci.setStatus(EnumStatus.CREATED));
            routingComponentIndexTodoList = datasetInitializer.createRoutingComponentIndex(null, todoList.getTechnicalId(), rci -> rci.setStatus(EnumStatus.CREATED));
        });
    }

    @Test
    public void deleteRoutingComponentIndex_RC() throws FunctionalException {
        routingComponentIndexService.deleteRoutingComponent(routingComponentIndexRC.getNaturalId());
        assertThat(routingComponentIndexRepository.findLastVersionByNaturalId(routingComponentIndexRC.getNaturalId()), equalTo(Optional.empty()));
        assertThat(routingComponentRepository.findById(routingComponent.getNaturalId()), equalTo(Optional.empty()));
    }

    @Test
    public void deleteRoutingComponentIndex_TodoList() throws FunctionalException {
        routingComponentIndexService.deleteRoutingComponent(routingComponentIndexTodoList.getNaturalId());
        assertThat(routingComponentIndexRepository.findLastVersionByNaturalId(routingComponentIndexTodoList.getNaturalId()), equalTo(Optional.empty()));
        assertThat(todoListRepository.findById(todoList.getNaturalId()), equalTo(Optional.empty()));
    }
}
