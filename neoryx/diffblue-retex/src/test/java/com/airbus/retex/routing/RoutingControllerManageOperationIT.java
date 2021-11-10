package com.airbus.retex.routing;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.functionalAreaName.FunctionalAreaNameFieldsEnum;
import com.airbus.retex.business.dto.operation.ManageOperationDto;
import com.airbus.retex.business.dto.operation.OperationCreationDto;
import com.airbus.retex.business.dto.operation.OperationDto;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.persistence.routing.RoutingRepository;
import com.airbus.retex.utils.ConstantUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
public class RoutingControllerManageOperationIT extends BaseControllerTest {

    @Autowired
    private RoutingRepository routingRepository;

    private ManageOperationDto manageOperationDto;

    private Operation operation;

    @BeforeEach
    public void before() {
        asUser = dataset.user_simpleUser;

        runInTransaction(() -> {
            dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.WRITE);

            dataset.routing_1.addOperation(dataset.operation_1);
            dataset.routing_1.addOperation(dataset.operation_2);
            dataset.routing_1.addOperation(dataset.operation_3_todo_list);
            dataset.operation_3_todo_list.addTodoList(dataSetInitializer.createTodoList());

            List<OperationDto> operationDtos = new ArrayList<>();
            operationDtos.add(convertOperationToDto(dataset.operation_1));
            operationDtos.add(convertOperationToDto(dataset.operation_2));
            operationDtos.add(convertOperationToDto(dataset.operation_3_todo_list));


            List<OperationCreationDto> operationCreationDtoList = new ArrayList<>();
            List<Long> operationDeleteList = new ArrayList<>();
            manageOperationDto = new ManageOperationDto();
            manageOperationDto.setOperations(operationDtos);
            manageOperationDto.setAddedOperations(operationCreationDtoList);
            manageOperationDto.setDeletedOperations(operationDeleteList);

            operation = initOperationWithoutPostAssociation();
        });
    }

    @Test
    public void manageOperationEmptyObjectWhithRole() throws Exception {
        withRequest = put(ConstantUrl.API_ROUTINGS_OPERATIONS, dataset.routing_1.getNaturalId()).content(objectMapper.writeValueAsString(manageOperationDto));
        expectedStatus = HttpStatus.OK;

        abstractCheck()
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].orderNumber").isNumber())
                .andExpect(jsonPath("$[0].operationType").isNotEmpty())
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].orderNumber").isNumber())
                .andExpect(jsonPath("$[1].operationType").isNotEmpty())
                .andExpect(jsonPath("$[2].id").isNumber())
                .andExpect(jsonPath("$[2].orderNumber").isNumber())
                .andExpect(jsonPath("$[2].operationType").isNotEmpty());
    }

    @Test
    public void manageOperationEmptyObjectWhithoutRole() throws Exception {
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.NONE);

        abstractCheckManageOperation(HttpStatus.FORBIDDEN);
    }

    @Test
    public void manageOperationEmptyObjectWhithoutWriteLevel() throws Exception {
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.READ);

        abstractCheckManageOperation(HttpStatus.FORBIDDEN);
    }

    @Test
    public void manageOperationAddOneOperation() throws Exception {
        manageOperationDto.getAddedOperations().add(initCreationOperationDto(4));

        abstractCheckManageOperation(HttpStatus.OK);
    }

    @Test
    public void manageOperationRemoveOneOperation() throws Exception {
        manageOperationDto.getDeletedOperations().add(dataset.operation_3_todo_list.getNaturalId());
        expectedStatus = HttpStatus.OK;
        withRequest = put(ConstantUrl.API_ROUTINGS_OPERATIONS, dataset.operation_1.getRouting().getNaturalId()).content(objectMapper.writeValueAsString(manageOperationDto));

        abstractCheck();
    }

    @Test
    public void manageOperationAddOperationKo() throws Exception {
        manageOperationDto.getOperations().add(initOperationDto(1));
        manageOperationDto.getAddedOperations().add(initCreationOperationDto(1));

        withRequest = put(ConstantUrl.API_ROUTINGS_OPERATIONS, dataset.routing_1.getNaturalId()).content(objectMapper.writeValueAsString(manageOperationDto));
        expectedStatus = HttpStatus.BAD_REQUEST;

        abstractCheck();
    }

    @Test
    public void manageOperationUpdateOk() throws Exception {

        dataset.routing_1.setStatus(EnumStatus.CREATED);
        routingRepository.save(dataset.routing_1);

        if (null != manageOperationDto.getOperations()) {
            manageOperationDto.getOperations().get(0).setOrderNumber(5);
        }
        withRequest = put(ConstantUrl.API_ROUTINGS_OPERATIONS, dataset.routing_1.getNaturalId()).content(objectMapper.writeValueAsString(manageOperationDto));
        expectedStatus = HttpStatus.OK;

        abstractCheck()
                .andExpect(jsonPath("$[0].id").value(dataset.operation_2.getNaturalId()))
                .andExpect(jsonPath("$[0].orderNumber").value(1L))
                .andExpect(jsonPath("$[1].id").value(dataset.operation_3_todo_list.getNaturalId()))
                .andExpect(jsonPath("$[1].orderNumber").value(2L))
                .andExpect(jsonPath("$[2].id").value(dataset.operation_1.getNaturalId()))
                .andExpect(jsonPath("$[2].orderNumber").value(3L)); // We have 3 records the order_number 4 should be change in 3
    }

    @Test
    public void manageOperationUpdateKo() throws Exception {
        if (null != manageOperationDto.getOperations()) {
            manageOperationDto.getOperations().get(0).setOrderNumber(2);
        }

        abstractCheckManageOperation(HttpStatus.BAD_REQUEST);
    }

    @Disabled
    @Test
    public void manageOperationAddOneOperationTodoList() throws Exception {

        dataset.routing_1.setStatus(EnumStatus.CREATED);
        routingRepository.save(dataset.routing_1);
        manageOperationDto.getAddedOperations().add(initCreationOperationTodoListDto(4, dataset.routing_1));
        withRequest = put(ConstantUrl.API_ROUTINGS_OPERATIONS, dataset.routing_1.getNaturalId()).content(objectMapper.writeValueAsString(manageOperationDto));
        expectedStatus = HttpStatus.OK;

        abstractCheck()
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].operationTodoLists").isNotEmpty())
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].operationTodoLists").isEmpty())
                .andExpect(jsonPath("$[2].id").isNumber())
                .andExpect(jsonPath("$[2].operationTodoLists").isEmpty())
                .andExpect(jsonPath("$[3].id").isNumber())
                .andExpect(jsonPath("$[3].operationTodoLists[0].todoList").isNotEmpty())
                .andExpect(jsonPath("$[3].operationTodoLists[0].todoList.id").isNumber())
                .andExpect(jsonPath("$[3].operationTodoLists[0].todoList.todoListName").isNotEmpty())
                .andExpect(jsonPath("$[3].operationTodoLists[0].todoList.todoListName.id").isNumber());
    }

    @Test
    public void getAllOperationsOK() throws Exception {
        FunctionalArea functionalAreaName = dataSetInitializer.createFunctionalArea(functionalArea -> functionalArea.setPart(dataset.part_link_routing));
        dataSetInitializer.createTranslate(functionalAreaName, FunctionalAreaNameFieldsEnum.name, Language.EN, "EN functional");
        dataSetInitializer.createTranslate(functionalAreaName, FunctionalAreaNameFieldsEnum.name, Language.FR, "FR functional");

        withRequest = get(ConstantUrl.API_ROUTINGS_OPERATIONS, dataset.routing_1.getNaturalId());
        expectedStatus = HttpStatus.OK;

        abstractCheck()
                .andExpect(jsonPath("$.operations[0]").isNotEmpty())
                .andExpect(jsonPath("$.operations[0].id").isNumber())
                .andExpect(jsonPath("$.operations[0].orderNumber").isNumber())
                .andExpect(jsonPath("$.operations[0].operationType").isNotEmpty())
                .andExpect(jsonPath("$.operations[0].operationType.id").isNumber())
                .andExpect(jsonPath("$.operations[0].operationType.template").isString())
                .andExpect(jsonPath("$.operations[0].operationType.name").isString())
                .andExpect(jsonPath("$.operations[0].operationType.behavior").isString())
                .andExpect(jsonPath("$.functionalAreas").isNotEmpty())
                .andExpect(jsonPath("$.functionalAreas[0].id").isNumber())
                .andExpect(jsonPath("$.functionalAreas[0].areaNumber").isString())
                .andExpect(jsonPath("$.functionalAreas[0].functionalAreaName").isNotEmpty())
                .andExpect(jsonPath("$.functionalAreas[0].functionalAreaName.name").isString());
    }

    @Test
    public void getAllOperationsForbidden() throws Exception {
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.NONE);

        withRequest = get(ConstantUrl.API_ROUTINGS_OPERATIONS, dataset.routing_1.getNaturalId());
        expectedStatus = HttpStatus.FORBIDDEN;

        abstractCheck();
    }

    @Test
    public void getAllOperationsWithBadRoutingId() throws Exception {
        withRequest = get(ConstantUrl.API_ROUTINGS_OPERATIONS, 99L);
        expectedStatus = HttpStatus.NOT_FOUND;

        abstractCheck();
    }

    private void abstractCheckManageOperation(HttpStatus expectedHttpStatus) throws Exception {
        withRequest = put(ConstantUrl.API_ROUTINGS_OPERATIONS, dataset.routing_1.getNaturalId()).content(objectMapper.writeValueAsString(manageOperationDto));
        expectedStatus = expectedHttpStatus;
        abstractCheck();
    }

    private OperationDto initOperationDto(Integer orderNumber) {
        OperationDto operationUpdateDto= new OperationDto();
        operationUpdateDto.setId(dataset.operation_1.getNaturalId());
        operationUpdateDto.setOrderNumber(orderNumber);

        return operationUpdateDto;
    }

    private OperationDto convertOperationToDto(Operation operation) {
        OperationDto operationUpdateDto= new OperationDto();
        operationUpdateDto.setId(operation.getNaturalId());
        operationUpdateDto.setOrderNumber(operation.getOrderNumber());

        return operationUpdateDto;
    }

    private OperationCreationDto initCreationOperationDto(Integer orderNumber) {
        OperationCreationDto operationCreationDto = new OperationCreationDto();
        operationCreationDto.setOperationTypeId(dataset.operation_1.getOperationType().getId());
        operationCreationDto.setOrderNumber(orderNumber);
        operationCreationDto.setRoutingId(dataset.operation_1.getRouting().getNaturalId());

        return operationCreationDto;
    }

    private OperationCreationDto initCreationOperationTodoListDto(Integer orderNumber, Routing routing) {
        Operation operation = dataSetInitializer.createOperation(orderNumber, operation1 -> {
            operation1.setOperationType(dataset.operationType_preliminary);
        });
        operation.addTodoList(dataSetInitializer.createTodoList());
        OperationCreationDto operationCreationDto = new OperationCreationDto();
        operationCreationDto.setOperationTypeId(operation.getOperationType().getId());
        operationCreationDto.setOrderNumber(orderNumber);
        operationCreationDto.setRoutingId(routing.getNaturalId());
        List<Long> todoListId = new ArrayList<>();
        todoListId.add(dataset.todoList_1.getRoutingComponentIndex().getNaturalId());
        operationCreationDto.setTodoListId(todoListId);
        return operationCreationDto;
    }

    private Operation initOperationWithoutPostAssociation(){
        // CREATION ROUTING
        Part part = dataSetInitializer.createPart(null);
        Routing routing = dataSetInitializer.createRouting(part);
        Operation operationWithoutPostAssociation = dataSetInitializer.createOperation(1, operation -> {
            operation.setRouting(routing);
        });
        FunctionalArea functionalAreaWithoutPostAssociation = dataSetInitializer.createFunctionalArea(functionalArea -> {
            functionalArea.setPart(part);
            functionalArea.setFunctionality(dataset.functionality_splines);
        });
        OperationFunctionalArea operationFunctionalArea = dataSetInitializer.createOperationFunctionalArea(opfa -> {
            opfa.setFunctionalArea(functionalAreaWithoutPostAssociation);
            opfa.setOperation(operationWithoutPostAssociation);
        });

        RoutingComponent routingComponent = dataSetInitializer.createRoutingComponent(rc -> {
            rc.setFunctionality(dataset.functionality_splines);
            rc.setOperationType(operationWithoutPostAssociation.getOperationType());
            rc.setDamageId(dataset.damage_corrosion.getTechnicalId());
            rc.setDamage(dataset.damage_corrosion);
        });

        dataSetInitializer.createRoutingComponentIndex(routingComponent.getTechnicalId(), null);

        Step stepOne = dataSetInitializer.createStep(step -> {
            step.setRoutingComponent(routingComponent);
            step.setFiles(new HashSet<>(Arrays.asList(dataSetInitializer.createMedia())));

        });

        dataSetInitializer.createPost(p -> {
            p.setStep(stepOne);
        }, "designationFR", "designationEN");

        dataSetInitializer.createStepActivation(true, stepOne, operationFunctionalArea);

        return operationWithoutPostAssociation;
    }

}
