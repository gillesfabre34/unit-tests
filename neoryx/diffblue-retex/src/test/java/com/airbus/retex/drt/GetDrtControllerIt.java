package com.airbus.retex.drt;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.dataset.DatasetFactory;
import com.airbus.retex.dataset.DrtDataset;
import com.airbus.retex.dataset.RoutingDataset;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.persistence.routing.RoutingRepository;
import com.airbus.retex.service.drt.IDrtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class GetDrtControllerIt extends BaseControllerTest {
    private Drt drt;
    private Drt drtInProgress;
    private DrtDataset drtValidate;
    private RoutingDataset routingDataset;
    private OperationFunctionalArea opFADim;

    private static final String WRONG_DRT_ID = "0";
    private static final String API_DRT_OPERATIONS = "/api/drts/{id}/operations";
    private static final String API_DRT_OPERATION_TASK_HEADER = API_DRT_OPERATIONS + "/{operationId}/tasks/{taskId}/header";

    @Autowired
    private IDrtService drtService;

    @Autowired
    private DatasetFactory datasetFactory;

    @Autowired
    private RoutingRepository routingRepository;

    @BeforeEach
    private void before() {
        routingDataset = datasetFactory.createRoutingDataset();

        ChildRequest childRequest = dataSetInitializer.createChildRequest(cr -> {
            cr.setRoutingNaturalId(routingDataset.routing.getNaturalId());
            cr.setStatus(EnumStatus.VALIDATED);
        });

        drt = dataSetInitializer.createDRT(drt1 -> {
            drt1.setChildRequest(childRequest);
            drt1.setStatus(EnumStatus.CREATED);
        });
        drtInProgress = dataSetInitializer.createDRT(drt1 -> {
            drt1.setRouting(routingDataset.routing);
            drt1.setStatus(EnumStatus.IN_PROGRESS);
        });

        runInTransaction(() -> {
            drtValidate = new DrtDataset();
            drtValidate.requestDataset = datasetFactory.createRequestDataset();
            drtValidate.drt = dataSetInitializer.createDRT(drt1 -> {
                drt1.setChildRequest(drtValidate.requestDataset.childRequest);
                drt1.setRouting(drtValidate.requestDataset.routingDataset.routing);
                drt1.setStatus(EnumStatus.VALIDATED);
            });

            drtValidate.drt.addOperationStatus(drtValidate.drtOperationStatusDimensional = dataSetInitializer.createDrtOperationStatusFunctionalArea(drtValidate.drt, drtValidate.requestDataset.routingDataset.operationFunctionalAreaDimensional, EnumStatus.VALIDATED));
            drtValidate.drt.addOperationStatus(drtValidate.drtOperationStatusTriDimensional = dataSetInitializer.createDrtOperationStatusFunctionalArea(drtValidate.drt, drtValidate.requestDataset.routingDataset.operationFunctionalAreaTridimensional, EnumStatus.VALIDATED));
            drtValidate.drt.addOperationStatus(drtValidate.drtOperationStatusLaboratory = dataSetInitializer.createDrtOperationStatusFunctionalArea(drtValidate.drt, drtValidate.requestDataset.routingDataset.operationFunctionalAreaLaboratory, EnumStatus.VALIDATED));
            drtValidate.drt.addOperationStatus(drtValidate.drtOperationStatusVisual = dataSetInitializer.createDrtOperationStatusFunctionalArea(drtValidate.drt, drtValidate.requestDataset.routingDataset.operationFunctionalAreaVisual, EnumStatus.VALIDATED));

            drtValidate.drt.addOperationStatus(drtValidate.drtOperationStatusPreliminary = dataSetInitializer.createDrtOperationStatusTodoList(drtValidate.drt, drtValidate.requestDataset.routingDataset.operationPreliminary, drtValidate.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListPreliminary, EnumStatus.VALIDATED));
            drtValidate.drt.addOperationStatus(drtValidate.drtOperationStatusClosure = dataSetInitializer.createDrtOperationStatusTodoList(drtValidate.drt, drtValidate.requestDataset.routingDataset.operationClosure, drtValidate.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListClosure, EnumStatus.VALIDATED));

        });

        opFADim = routingDataset.operationDimensional.getOperationFunctionalAreas().iterator().next();

        dataSetInitializer.createUserFeature(FeatureCode.DRT_INTERNAL_INSPECTION, dataset.user_simpleUser, EnumRightLevel.READ);
        asUser = dataset.user_simpleUser;
    }

    @Test
    public void getAllOperationsByDrt_ok() throws Exception {
        withRequest = get(API_DRT_OPERATIONS, drt.getId());
        expectedStatus = HttpStatus.OK;
        checkOperations();
    }

    @Test
    public void getAllOperationsByDrtInProgress_ok() throws Exception {

        withRequest = get(API_DRT_OPERATIONS, drtInProgress.getId());
        expectedStatus = HttpStatus.OK;
        checkOperations();
    }

    @Test
    public void getAllOperationsByDrt_ko_forbidden() throws Exception {
        asUser = dataset.user_simpleUser2;
        withRequest = get(API_DRT_OPERATIONS, drt.getId());
        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck().andReturn().getResolvedException().getMessage().equals("Access is denied");
    }

    @Test
    public void getAllOperationsByDrt_ko_wrongDrtId() throws Exception {
        withRequest = get(API_DRT_OPERATIONS, WRONG_DRT_ID);
        expectedStatus = HttpStatus.NOT_FOUND;
        abstractCheck().andExpect(jsonPath("$.messages").value("The given DRT is not found"));
    }

    @Test
    public void getAllOperationsByDrt_ko_wrongRoutingId() throws Exception {
        drt = dataSetInitializer.createDRT(drt1 -> {
            drt1.setChildRequest(dataSetInitializer.createChildRequest(cr -> cr.setRoutingNaturalId(0L)));
            drt1.setStatus(EnumStatus.CREATED);
        });

        withRequest = get(API_DRT_OPERATIONS, drt.getId());
        expectedStatus = HttpStatus.BAD_REQUEST;

        abstractCheck().andExpect(jsonPath("$.messages").value("No routing found for this DRT"));
    }

    @Test
    public void getAllOperationsByDrtInProgress_ko_wrongRoutingId() throws Exception {
        drt = dataSetInitializer.createDRT(drt1 -> {
            drt1.setRouting(null);
            drt1.setStatus(EnumStatus.IN_PROGRESS);
        });
        withRequest = get(API_DRT_OPERATIONS, drt.getId());
        expectedStatus = HttpStatus.BAD_REQUEST;

        abstractCheck().andExpect(jsonPath("$.messages").value("No routing found for this DRT"));
    }


    @Test
    public void getAllOperationsByDrt_OtherRoutingId() throws Exception {
        drt = dataSetInitializer.createDRT(drt1 -> {
            drt1.setChildRequest(dataSetInitializer.createChildRequest());
            drt1.setStatus(EnumStatus.CREATED);
        });
        withRequest = get(API_DRT_OPERATIONS, drt.getId());
        expectedStatus = HttpStatus.OK;

        abstractCheck()
                .andExpect(jsonPath("$.operations", hasSize(not(6))));
    }


    @Test
    public void getAllOperationsByDrtInProgress_OtherRoutingId() throws Exception {
        drt = dataSetInitializer.createDRT(drt1 -> {
            drt1.setRouting(dataSetInitializer.createRouting(dataSetInitializer.createPart(null)));
            drt1.setStatus(EnumStatus.IN_PROGRESS);
        });
        withRequest = get(API_DRT_OPERATIONS, drt.getId());
        expectedStatus = HttpStatus.OK;

        abstractCheck()
                .andExpect(jsonPath("$.operations", hasSize(not(6))));
    }

    private void checkOperations() throws Exception {
        abstractCheck().andExpect(jsonPath("$.operations", hasSize(6)))
                .andExpect(jsonPath("$.operations[0].orderNumber").value(1))
                .andExpect(jsonPath("$.operations[0].operationType.name").value("Dimensional"))
                .andExpect(jsonPath("$.operations[0].operationType.template").value("dimensional"))
                .andExpect(jsonPath("$.operations[0].operationType.behavior").value("ROUTING_COMPONENT"))
                .andExpect(jsonPath("$.operations[1].orderNumber").value(2))
                .andExpect(jsonPath("$.operations[1].operationType.name").value("Laboratory"))
                .andExpect(jsonPath("$.operations[1].operationType.template").value("laboratory"))
                .andExpect(jsonPath("$.operations[1].operationType.behavior").value("ROUTING_COMPONENT"))
                .andExpect(jsonPath("$.operations[2].orderNumber").value(3))
                .andExpect(jsonPath("$.operations[2].operationType.name").value("TriDimensional"))
                .andExpect(jsonPath("$.operations[2].operationType.template").value("tridimensional"))
                .andExpect(jsonPath("$.operations[2].operationType.behavior").value("ROUTING_COMPONENT"))
                .andExpect(jsonPath("$.operations[3].orderNumber").value(4))
                .andExpect(jsonPath("$.operations[3].operationType.name").value("Visual"))
                .andExpect(jsonPath("$.operations[3].operationType.template").value("visual"))
                .andExpect(jsonPath("$.operations[3].operationType.behavior").value("VISUAL_COMPONENT"))
                .andExpect(jsonPath("$.operations[4].orderNumber").value(5))
                .andExpect(jsonPath("$.operations[4].operationType.name").value("Preliminary"))
                .andExpect(jsonPath("$.operations[4].operationType.template").value("preliminary"))
                .andExpect(jsonPath("$.operations[4].operationType.behavior").value("TODO_LIST"))
                .andExpect(jsonPath("$.operations[5].orderNumber").value(6))
                .andExpect(jsonPath("$.operations[5].operationType.name").value("Closure"))
                .andExpect(jsonPath("$.operations[5].operationType.template").value("closure"))
                .andExpect(jsonPath("$.operations[5].operationType.behavior").value("TODO_LIST"));
    }

    //---------------- GET Operation Task Header Name ---------------------//

    @Test
    public void getOperationTaskHeaderValues_ok() throws Exception {
        withRequest = get(API_DRT_OPERATION_TASK_HEADER,
                drt.getId(),
                opFADim.getOperation().getNaturalId(),
                opFADim.getFunctionalArea().getNaturalId());
        expectedStatus = HttpStatus.OK;
        checkOperationTaskHeaderValues();
    }


    @Test
    public void getOperationTaskHeaderValues_ko_forbidden() throws Exception {
        asUser = dataset.user_simpleUser2;
        withRequest = get(API_DRT_OPERATION_TASK_HEADER,
                drt.getId(),
                opFADim.getOperation().getNaturalId(),
                opFADim.getFunctionalArea().getNaturalId());
        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck().andReturn().getResolvedException().getMessage().equals("Access is denied");
    }

    @Test
    public void getOperationTaskHeader_ko_DrtNotFound() throws Exception {
        withRequest = get(API_DRT_OPERATION_TASK_HEADER,
                WRONG_DRT_ID,
                opFADim.getOperation().getNaturalId(),
                opFADim.getFunctionalArea().getNaturalId());
        expectedStatus = HttpStatus.NOT_FOUND;
        abstractCheck().andExpect(jsonPath("$.messages").value("The given DRT is not found"));
    }

    @Test
    public void getOperationTaskHeader_ko_RoutingOfDrtNotFound() throws Exception {
        drt = dataSetInitializer.createDRT(drt1 -> {
            drt1.setChildRequest(dataSetInitializer.createChildRequest(cr -> cr.setRoutingNaturalId(0L)));
            drt1.setStatus(EnumStatus.CREATED);
        });
        withRequest = get(API_DRT_OPERATION_TASK_HEADER,
                drt.getId(),
                opFADim.getOperation().getNaturalId(),
                opFADim.getFunctionalArea().getNaturalId());
        expectedStatus = HttpStatus.BAD_REQUEST;

        abstractCheck().andExpect(jsonPath("$.messages").value("No routing found for this DRT"));
    }

    @Test
    public void getOperationTodoListName_ok() throws Exception {
        withRequest = get(API_DRT_OPERATIONS, drt.getId());
        expectedStatus = HttpStatus.OK;
        abstractCheck().andExpect(jsonPath("$.operations[4].operationTodoLists[0].todoList.todoListName.id")
                .value(dataset.todoListName_1.getId()))
                .andExpect(jsonPath("$.operations[4].operationTodoLists[0].todoList.todoListName.name")
                .value("TodoListName name number " + dataset.todoListName_1.getId()));
    }

    @Test
    public void getIsClosableFalse() throws Exception {
        withRequest = get(API_DRT_OPERATIONS, drt.getId());
        expectedStatus = HttpStatus.OK;
        abstractCheck().andExpect(jsonPath("$.closable")
                .value(false));
    }

    @Test
    public void getIsClosableTrue() throws Exception {
        withRequest = get(API_DRT_OPERATIONS, drtValidate.drt.getId());
        expectedStatus = HttpStatus.OK;
        abstractCheck().andExpect(jsonPath("$.closable")
                .value(true));
    }

    @Test
    public void getOperationTaskHeader_ko_OperationFoundNotUnique_NoOperation() throws Exception {
        withRequest = get(API_DRT_OPERATION_TASK_HEADER,
                drt.getId(),
                0L,
                opFADim.getFunctionalArea().getNaturalId());
        expectedStatus = HttpStatus.BAD_REQUEST;

        abstractCheck().andExpect(jsonPath("$.messages").value("Operation not unique for this DRT"));
    }

    @Test
    public void getOperationTaskHeader_ko_OperationFunctionalAreaFoundNotUnique_NoOperation() throws Exception {
        withRequest = get(API_DRT_OPERATION_TASK_HEADER,
                drt.getId(),
                opFADim.getOperation().getNaturalId(),
                0L);
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck().andExpect(jsonPath("$.messages").value("Operation Functional Area not unique for this DRT"));
    }

    @Test
    public void getOperationTaskHeader_ko_OperationFunctionalAreaFoundNotUnique_MultipleOperations() throws Exception {
        duplicateOperationFunctionalArea(opFADim.getFunctionalArea());
        withRequest = get(API_DRT_OPERATION_TASK_HEADER,
                drt.getId(),
                opFADim.getOperation().getNaturalId(),
                opFADim.getFunctionalArea().getNaturalId());
        expectedStatus = HttpStatus.BAD_REQUEST;

        abstractCheck().andExpect(jsonPath("$.messages").value("Operation Functional Area not unique for this DRT"));
    }

    @Test
    public void getOperationTaskHeader_ko_OperationTypeNotAllows() throws Exception {
        withRequest = get(API_DRT_OPERATION_TASK_HEADER,
                drt.getId(),
                routingDataset.operationPreliminary.getNaturalId(),
                opFADim.getFunctionalArea().getNaturalId());
        expectedStatus = HttpStatus.BAD_REQUEST;

        abstractCheck().andExpect(jsonPath("$.messages").value("Operation type not allows"));
    }

    private void checkOperationTaskHeaderValues() throws Exception {
        abstractCheck()
                .andExpect(jsonPath("$.areaNumber").value(opFADim.getFunctionalArea().getAreaNumber()))
                .andExpect(jsonPath("$.functionality.id").value(opFADim.getFunctionalArea().getFunctionality().getId()))
                .andExpect(jsonPath("$.classification").value(opFADim.getFunctionalArea().getClassification().name()))
                .andExpect(jsonPath("$.material").value(opFADim.getFunctionalArea().getMaterial()))
                .andExpect(jsonPath("$.treatment.id").value(opFADim.getFunctionalArea().getTreatment().getId()))
                .andExpect(jsonPath("$.functionalAreaName.id").value(opFADim.getFunctionalArea().getFunctionalAreaName().getId()));
    }

    private void duplicateOperationFunctionalArea(FunctionalArea functionalArea) {
        OperationFunctionalArea operationFunctionalAreaDim = dataSetInitializer.createOperationFunctionalArea(opfa -> {
            opfa.setFunctionalArea(functionalArea);
            opfa.setOperation(routingDataset.operationDimensional);
        });
        routingDataset.operationDimensional.addOperationFunctionalArea(operationFunctionalAreaDim);
    }
}
