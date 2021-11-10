package com.airbus.retex.service.drt;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.dataset.DatasetFactory;
import com.airbus.retex.dataset.RoutingDataset;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.drt.DrtOperationStatusFunctionalArea;
import com.airbus.retex.model.drt.DrtOperationStatusTodoList;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.persistence.drt.DrtRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UpdateDrtServiceIt extends AbstractServiceIT {
    private RoutingDataset routingDataset;
    private Drt drt;
    private Filtering filtering;

    @Autowired
    private IDrtService drtService;

    @Autowired
    private DatasetFactory datasetFactory;

    @Autowired
    private DrtRepository drtRepository;

    @BeforeEach
    private void before() {
        runInTransaction(() -> {
            routingDataset = datasetFactory.createRoutingDataset(false);

            ChildRequest childRequest = dataSetInitializer.createChildRequest(cr -> {
                cr.setRoutingNaturalId(routingDataset.routing.getNaturalId());
                cr.setStatus(EnumStatus.VALIDATED);
            });


            drt = dataSetInitializer.createDRT(drt1 -> {
                drt1.setChildRequest(childRequest);
                drt1.setStatus(EnumStatus.IN_PROGRESS);
                drt1.setRouting(routingDataset.routing);
            });

            filtering = dataSetInitializer.createFiltering(filtering1 -> {
                filtering1.setDrt(drt);
            });
        });
    }

    @Test
    public void validateDrt_notEnoughDrtOperationStatus() {
        // 0 DrtOperationStatus and 0 OperationFunctionalArea and 0 Operation Todolist
        drtService.validateDrt(drt);
        assertEquals(EnumStatus.IN_PROGRESS, drt.getStatus());

        // 0 DrtOperationStatus and 1 OperationFunctionalArea and 0 Operation Todolist
        addOperationDimensional(1);
        drtService.validateDrt(drt);
        assertEquals(EnumStatus.IN_PROGRESS, drt.getStatus());
    }

    @Test
    public void updateValidateDrt_drtOperationStatus_notAllSameStatus() {
        // 2 DrtOperationStatus and 2 OperationFunctionalArea and 0 Operation Todolist
        addDrtOperationStatus(EnumStatus.VALIDATED, EnumStatus.IN_PROGRESS);

        drtService.validateDrt(drt);
        assertEquals(EnumStatus.IN_PROGRESS, drt.getStatus());
    }

    @Test
    public void updateValidateDrt_updateSuccess() {
        // 2 DrtOperationStatus and 2 OperationFunctionalArea and 0 Operation Todolist
        addDrtOperationStatus(EnumStatus.VALIDATED, EnumStatus.VALIDATED);

        drtService.validateDrt(drt);
        assertEquals(EnumStatus.VALIDATED, drt.getStatus());
    }

    @Test
    public void updateCloseDrt_drtOperationStatus_notAllSameStatus_1() {
        // 2 DrtOperationStatus and 2 OperationFunctionalArea and 0 Operation Todolist
        addDrtOperationStatus(EnumStatus.VALIDATED, EnumStatus.IN_PROGRESS);

        FunctionalException thrown = assertThrows(FunctionalException.class, () -> drtService.closeDrt(drt.getId()));
        assertEquals("retex.error.drt.not.closable", thrown.getMessage());

        assertEquals(EnumStatus.IN_PROGRESS, drt.getStatus());
    }

    @Test
    public void updateCloseDrt_drtOperationStatus_notAllSameStatus_2() {
        // 2 DrtOperationStatus and 2 OperationFunctionalArea and 0 Operation Todolist
        addDrtOperationStatus(EnumStatus.VALIDATED, EnumStatus.VALIDATED);

        FunctionalException thrown = assertThrows(FunctionalException.class, () -> drtService.closeDrt(drt.getId()));
        assertEquals("retex.error.drt.not.closable", thrown.getMessage());

        assertEquals(EnumStatus.IN_PROGRESS, drt.getStatus());
    }

    @Test
    public void updateCloseDrt_updateSuccess() throws FunctionalException {
        // 2 DrtOperationStatus and 2 OperationFunctionalArea and 0 Operation Todolist
        addDrtOperationStatus(EnumStatus.VALIDATED, EnumStatus.VALIDATED);
        drtService.validateDrt(drt);
        assertEquals(EnumStatus.VALIDATED, drt.getStatus());
        drt = drtRepository.save(drt);
        drtService.closeDrt(drt.getId());
        drt = drtRepository.findById(drt.getId()).get();
        assertEquals(EnumStatus.CLOSED, drt.getStatus());
    }

    private void addDrtOperationStatus(EnumStatus status1, EnumStatus status2) {
        runInTransaction(() -> {
            int operationNumber = 1;

            addOperationDimensional(operationNumber);
            DrtOperationStatusFunctionalArea drtOFA1 = dataSetInitializer.createDrtOperationStatusFunctionalArea(
                drt,
                routingDataset.operationFunctionalAreaDimensional
            );
            drtOFA1.setStatus(status1);
            drt.addOperationStatus(drtOFA1);

            addOperationTridimensionel(operationNumber);
            DrtOperationStatusFunctionalArea drtOFA2 = dataSetInitializer.createDrtOperationStatusFunctionalArea(
                drt,
                routingDataset.operationFunctionalAreaTridimensional
            );
            drtOFA2.setStatus(status2);
            drt.addOperationStatus(drtOFA2);
        });
    }

    private void addOperationDimensional(int operationNumber) {
        //Dimensional
        routingDataset.operationDimensional = dataSetInitializer.createOperation(operationNumber++, operation -> {
            operation.setOperationType(dataSetInitializer.getDataset().operationType_dimensional);
            operation.setRouting(routingDataset.routing);
        });
        routingDataset.operationFunctionalAreaDimensional = dataSetInitializer.createOperationFunctionalArea(operationFunctionalArea -> {
            operationFunctionalArea.setOperation(routingDataset.operationDimensional);
            operationFunctionalArea.setFunctionalArea(routingDataset.partDataset.functionalArea);
        });
        routingDataset.operationDimensional.addOperationFunctionalArea(routingDataset.operationFunctionalAreaDimensional);
        routingDataset.stepActivationDimensional = dataSetInitializer.createStepActivation(true, routingDataset.partDataset.routingComponentDataset.stepRoutingComponentDimensional, routingDataset.operationFunctionalAreaDimensional);
        routingDataset.routingFunctionalAreaPostDimensional = dataSetInitializer.createRoutingFunctionalAreaPost(routingFunctionalAreaPost -> {
            routingFunctionalAreaPost.setStepActivation(routingDataset.stepActivationDimensional);
            routingFunctionalAreaPost.setPost(routingDataset.partDataset.routingComponentDataset.postRoutingComponentDimensional);
        });

    }

    private void addOperationTridimensionel(int operationNumber) {
        //Tridimensional
        routingDataset.operationTridimensional = dataSetInitializer.createOperation(operationNumber++, operation -> {
            operation.setOperationType(dataSetInitializer.getDataset().operationType_tridimensional);
            operation.setRouting(routingDataset.routing);
        });
        routingDataset.operationFunctionalAreaTridimensional = dataSetInitializer.createOperationFunctionalArea(operationFunctionalArea -> {
            operationFunctionalArea.setOperation(routingDataset.operationTridimensional);
            operationFunctionalArea.setFunctionalArea(routingDataset.partDataset.functionalArea);
        });
        routingDataset.operationTridimensional.addOperationFunctionalArea(routingDataset.operationFunctionalAreaTridimensional);
        routingDataset.stepActivationTridimensional = dataSetInitializer.createStepActivation(true, routingDataset.partDataset.routingComponentDataset.stepRoutingComponentTridimensional, routingDataset.operationFunctionalAreaTridimensional);
        routingDataset.routingFunctionalAreaPostTridimensional = dataSetInitializer.createRoutingFunctionalAreaPost(routingFunctionalAreaPost -> {
            routingFunctionalAreaPost.setStepActivation(routingDataset.stepActivationTridimensional);
            routingFunctionalAreaPost.setPost(routingDataset.partDataset.routingComponentDataset.postRoutingComponentTridimensional);
        });

    }
}
