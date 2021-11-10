package com.airbus.retex.service.routing;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.routing.RoutingCreatedDto;
import com.airbus.retex.business.dto.routing.RoutingCreationDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routing.RoutingFieldsEnum;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.persistence.operation.OperationRepository;
import com.airbus.retex.persistence.operationType.OperationTypeRepository;
import com.airbus.retex.persistence.routing.RoutingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@Transactional
public class DuplicateRoutingServiceIT extends AbstractServiceIT {

    @Autowired
    private IRoutingService routingService;
    @Autowired
    private OperationRepository operationRepository;
    @Autowired
    private RoutingRepository routingRepository;
    @Autowired
    private OperationTypeRepository operationTypeRepository;

    private RoutingCreationDto routingCreationDto;
    private Routing routing1;

    private Operation withOperation;
    private Operation withOperation1;
    private Operation withOperation2;

    private TodoList todoList;
    private Part part_link_routing4;
    private Part part_link_routing5;

    private final List<OperationType> sourceOperationTypeTodo = new ArrayList<>();

    @BeforeEach
    public void before() {
        part_link_routing4 = dataSetInitializer.createPart(null);
        part_link_routing5 = dataSetInitializer.createPart(null);
        todoList = dataSetInitializer.createTodoList();

        routingCreationDto = new RoutingCreationDto();
        routingCreationDto.setPartId(dataset.part_example.getNaturalId());
        routingCreationDto.setPartNumberRoot(null);
        Map<RoutingFieldsEnum, String> mapFR = new HashMap<>();
        mapFR.put(RoutingFieldsEnum.name, "gamme 1");
        Map<RoutingFieldsEnum, String> mapEN = new HashMap<>();
        mapEN.put(RoutingFieldsEnum.name, "routing 1");
        Map<Language, Map<RoutingFieldsEnum, String>> translatedFields = new HashMap<>();
        translatedFields.put(Language.FR, mapFR);
        translatedFields.put(Language.EN, mapEN);
        routingCreationDto.setTranslatedFields(translatedFields);
    }

    private void createRoutingWithOneOperation() {
        routing1 = dataSetInitializer.createRouting(part_link_routing4);
        withOperation = dataSetInitializer.createOperation(1, ope -> {
            ope.setOperationType(dataset.operationType_preliminary);
            sourceOperationTypeTodo.add(dataset.operationType_preliminary);

        });
        withOperation.addTodoList(todoList);
        routing1.addOperation(withOperation);
    }

    private void createRoutingWithTwoSameOperation() {
        createRoutingWithOneOperation();
        withOperation1 = dataSetInitializer.createOperation(2, ope -> {
            ope.setOperationType(dataset.operationType_preliminary);
            sourceOperationTypeTodo.add(dataset.operationType_preliminary);
        });
        withOperation1.addTodoList(todoList);
        routing1.addOperation(withOperation1);
    }

    private void createRoutingWithTwoDifferentOperation() {
        createRoutingWithOneOperation();
        withOperation2 = dataSetInitializer.createOperation(2, ope -> {
            ope.setOperationType(dataset.operationType_preliminary);
            sourceOperationTypeTodo.add(dataset.operationType_preliminary);
        });
        withOperation2.addTodoList(todoList);
        routing1.addOperation(withOperation2);
    }

    private void checkCopyOperationTypeTodo(List<Operation> sourceTodoOperation, Routing copiedRouting, Long expectedCopiedOperation) {
        List<Operation> operations = operationRepository.findOperationByRoutingTechnicalId(copiedRouting.getTechnicalId());
        assertThat(operations, hasSize(Math.toIntExact(expectedCopiedOperation)));

        List<Long> sourceOperationTypeTodoIds = sourceOperationTypeTodo.stream().map(OperationType::getId).collect(Collectors.toList());
        List<Operation> copiedTodoOperation = operations.stream()
                .filter(operation -> sourceOperationTypeTodoIds.contains(operation.getOperationType().getId()))
                .sorted(Comparator.comparing(Operation::getOrderNumber))
                .collect(Collectors.toList());

        assertThat(copiedTodoOperation, hasSize(sourceTodoOperation.size()));

        for (var index = 0; index < sourceTodoOperation.size(); index++) {
            assertThat(copiedTodoOperation.get(index).getOperationType().getId(), equalTo(sourceTodoOperation.get(index).getOperationType().getId()));
            assertThat(copiedTodoOperation.get(index).getTodoLists().contains(todoList), is(true));
        }
    }

    @Test
    public void duplicate_ok_oneOperation() throws FunctionalException {
        createRoutingWithOneOperation();
        RoutingCreatedDto createdDto = routingService.duplicateRouting(routingCreationDto, dataset.user_superAdmin, routing1.getNaturalId());
        List<Operation> sourceTodoOperation = List.of(withOperation);

        allAssert(createdDto);

        Routing copiedRouting = routingRepository.findLastVersionByNaturalId(createdDto.getRoutingIds().get(0)).orElse(null);
        checkCopyOperationTypeTodo(sourceTodoOperation, copiedRouting, 5L);
    }

    @Test
    public void duplicate_ok_twoSameOperation() throws FunctionalException {
        createRoutingWithTwoSameOperation();
        RoutingCreatedDto createdDto = routingService.duplicateRouting(routingCreationDto, dataset.user_superAdmin, routing1.getNaturalId());
        List<Operation> sourceTodoOperation = List.of(withOperation, withOperation1);

        allAssert(createdDto);

        Routing copiedRouting = routingRepository.findLastVersionByNaturalId(createdDto.getRoutingIds().get(0)).orElse(null);
        checkCopyOperationTypeTodo(sourceTodoOperation, copiedRouting, 6L);
    }

    @Test
    public void duplicate_ok_twoDifferentOperation() throws FunctionalException {
        createRoutingWithTwoDifferentOperation();
        RoutingCreatedDto createdDto = routingService.duplicateRouting(routingCreationDto, dataset.user_superAdmin, routing1.getNaturalId());
        List<Operation> sourceTodoOperation = List.of(withOperation, withOperation2);

        allAssert(createdDto);
        Routing copiedRouting = routingRepository.findLastVersionByNaturalId(createdDto.getRoutingIds().get(0)).orElse(null);
        checkCopyOperationTypeTodo(sourceTodoOperation, copiedRouting, 6L);
    }

    @Test
    public void duplicate_ko_withOperationVisual() throws FunctionalException {
        RoutingCreatedDto createdDto = routingService.duplicateRouting(routingCreationDto, dataset.user_superAdmin, dataset.routing_3.getNaturalId());

        allAssert(createdDto);

        Routing copiedRouting = routingRepository.findLastVersionByNaturalId(createdDto.getRoutingIds().get(0)).orElse(null);
        List<Operation> operations = operationRepository.findOperationByRoutingTechnicalId(copiedRouting.getTechnicalId());
        assertThat(operations, hasSize(4));
    }

    private void allAssert(RoutingCreatedDto createdDto) {
        assertThat(createdDto.getErrorRoutingExists(), nullValue());
    }
}
