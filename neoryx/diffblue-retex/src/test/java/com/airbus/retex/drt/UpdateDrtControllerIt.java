package com.airbus.retex.drt;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.dataset.DatasetFactory;
import com.airbus.retex.dataset.RoutingDataset;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.drt.DrtOperationStatusFunctionalArea;
import com.airbus.retex.model.drt.DrtOperationStatusTodoList;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.persistence.drt.DrtRepository;
import com.airbus.retex.service.drt.IDrtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class UpdateDrtControllerIt extends BaseControllerTest {
    private RoutingDataset routingDataset;
    private Drt drt;

    @Autowired
    private IDrtService drtService;

    @Autowired
    private DatasetFactory datasetFactory;

    @Autowired
    private DrtRepository drtRepository;

    private static final String WRONG_DRT_ID = "0";
    private static final String API_DRT = "/api/drts/{id}";

    @BeforeEach
    private void before() {
        runInTransaction(() -> {
            Part part = dataSetInitializer.createPart(Set.of());
            Routing routing = dataSetInitializer.createRouting(r -> r.setStatus(EnumStatus.VALIDATED), part);

            routingDataset = datasetFactory.createRoutingDataset();

            ChildRequest childRequest = dataSetInitializer.createChildRequest(cr -> {
                cr.setRoutingNaturalId(routing.getNaturalId());
                cr.setStatus(EnumStatus.VALIDATED);
            });

            drt = dataSetInitializer.createDRT(drt1 -> {
                drt1.setChildRequest(childRequest);
                drt1.setStatus(EnumStatus.VALIDATED);
                drt1.setRouting(routing);
            });
        });
        asUser = dataSetInitializer.createUserWithRole("user1", "userName1", RoleCode.TECHNICAL_RESPONSIBLE);
    }


    @Test
    public void closeDrt_Ko_forbidden() throws Exception {
        asUser = dataset.user_simpleUser2;
        withRequest = put(API_DRT, drt.getId());
        expectedStatus = HttpStatus.FORBIDDEN;

        abstractCheck();
    }

    @Test
    public void closeDrt_KO_isNotClosable_OperationIsEmpty() throws Exception {
        withRequest = put(API_DRT, drt.getId());
        expectedStatus = HttpStatus.BAD_REQUEST;

        abstractCheck().andExpect(jsonPath("$.messages").value("DRT is not closable"));
    }

    @Test
    public void closeDrt_KO_WrongDrtId() throws Exception {
        withRequest = put(API_DRT, WRONG_DRT_ID);
        expectedStatus = HttpStatus.NOT_FOUND;

        abstractCheck().andExpect(jsonPath("$.messages").value("The given DRT is not found"));
    }


    private void addDrtAllOperationValidated() {
        routingDataset.routing.addOperation(routingDataset.operationDimensional);
        DrtOperationStatusFunctionalArea drtOFA1 = dataSetInitializer.createDrtOperationStatusFunctionalArea(
                drt,
                routingDataset.operationFunctionalAreaDimensional
        );
        drtOFA1.setStatus(EnumStatus.VALIDATED);
        drt.addOperationStatus(drtOFA1);

        routingDataset.routing.addOperation(routingDataset.operationTridimensional);
        DrtOperationStatusFunctionalArea drtOFA2 = dataSetInitializer.createDrtOperationStatusFunctionalArea(
                drt,
                routingDataset.operationFunctionalAreaTridimensional
        );
        drtOFA2.setStatus(EnumStatus.VALIDATED);
        drt.addOperationStatus(drtOFA2);


        routingDataset.routing.addOperation(routingDataset.operationLaboratory);
        DrtOperationStatusFunctionalArea drtOFA3 = dataSetInitializer.createDrtOperationStatusFunctionalArea(
                drt,
                routingDataset.operationFunctionalAreaLaboratory
        );
        drtOFA3.setStatus(EnumStatus.VALIDATED);
        drt.addOperationStatus(drtOFA3);

        routingDataset.routing.addOperation(routingDataset.operationVisual);
        DrtOperationStatusFunctionalArea drtOFA4 = dataSetInitializer.createDrtOperationStatusFunctionalArea(
                drt,
                routingDataset.operationFunctionalAreaVisual
        );
        drtOFA4.setStatus(EnumStatus.VALIDATED);
        drt.addOperationStatus(drtOFA4);

        routingDataset.routing.addOperation(routingDataset.operationPreliminary);
        DrtOperationStatusTodoList drtOTDL1 = dataSetInitializer.createDrtOperationStatusTodoList(
                drt,
                routingDataset.operationPreliminary,
                routingDataset.partDataset.routingComponentDataset.todoListPreliminary
        );
        drtOTDL1.setStatus(EnumStatus.VALIDATED);
        drt.addOperationStatus(drtOTDL1);

        routingDataset.routing.addOperation(routingDataset.operationClosure);
        DrtOperationStatusTodoList drtOTDL2 = dataSetInitializer.createDrtOperationStatusTodoList(
                drt,
                routingDataset.operationClosure,
                routingDataset.partDataset.routingComponentDataset.todoListClosure
        );
        drtOTDL2.setStatus(EnumStatus.VALIDATED);
        drt.addOperationStatus(drtOTDL2);
    }
}
