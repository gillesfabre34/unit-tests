package com.airbus.retex.service.drt;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.functionalArea.FunctionalAreaHeaderDto;
import com.airbus.retex.business.dto.operation.ListOperationDto;
import com.airbus.retex.business.dto.operation.OperationFullDto;
import com.airbus.retex.business.dto.operation.OperationLightDto;
import com.airbus.retex.business.exception.ConstantExceptionRetex;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.dataset.DatasetFactory;
import com.airbus.retex.dataset.RoutingDataset;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.persistence.routing.RoutingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GetDrtServiceIt extends AbstractServiceIT {
    private RoutingDataset routingDataset;
    private Drt drt;
    private Drt drtInProgress;
    private OperationFunctionalArea opFADim;

    private static final Long WRONG_DRT_ID = 0L;

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

            opFADim = routingDataset.operationDimensional.getOperationFunctionalAreas().iterator().next();
    }

    @Test
    public void getAllOperationsByDrt_ok() throws FunctionalException {
        assertTrue(checkOperationIds(drt.getId()));
    }

    @Test
    public void getAllOperationsByDrtInProgress_ok() throws FunctionalException {
        assertTrue(checkOperationIds(drtInProgress.getId()));
    }

    @Test
    public void getAllOperationsByDrt_ko_wrongDrtId() {
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            checkOperationIds(WRONG_DRT_ID);
        });

        assertEquals("retex.error.drt.not.found", thrown.getMessage());
    }

    @Test
    public void getAllOperationsByDrt_ko_wrongRoutingId() {
        drt = dataSetInitializer.createDRT(drt1 -> {
            drt1.setChildRequest(dataSetInitializer.createChildRequest(cr -> cr.setRoutingNaturalId(0L)));
            drt1.setStatus(EnumStatus.CREATED);
        });
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            checkOperationIds(drt.getId());
        });

        assertEquals("retex.error.drt.routing.not.found", thrown.getMessage());
    }

    @Test
    public void getAllOperationsByDrtInProgress_ko_wrongRoutingId() {
        drt = dataSetInitializer.createDRT(drt1 -> {
            drt1.setRouting(null);
            drt1.setStatus(EnumStatus.IN_PROGRESS);
        });
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            checkOperationIds(drt.getId());
        });

        assertEquals("retex.error.drt.routing.not.found", thrown.getMessage());
    }

    @Test
    public void getAllOperationsByDrt_OtherRoutingId() throws FunctionalException {
        drt = dataSetInitializer.createDRT(drt1 -> {
            drt1.setChildRequest(dataSetInitializer.createChildRequest());
            drt1.setStatus(EnumStatus.CREATED);
        });
        assertFalse(checkOperationIds(drt.getId()));
    }

    @Test
    public void getAllOperationsByDrtInProgress_OtherRoutingId() throws FunctionalException {
        drt = dataSetInitializer.createDRT(drt1 -> {
            drt1.setRouting(dataSetInitializer.createRouting(dataSetInitializer.createPart(null)));
            drt1.setStatus(EnumStatus.IN_PROGRESS);
        });
        assertFalse(checkOperationIds(drt.getId()));
    }

    private boolean checkOperationIds(Long drtId) throws FunctionalException {
        ListOperationDto listOperationDto = drtService.getOperationsByDrtId(drtId);
        List<Long> operationIds = listOperationDto.getOperations().stream().map(OperationLightDto::getId).collect(Collectors.toList());
        operationIds.addAll(listOperationDto.getFunctionalAreas().stream().map(functionalAreaDto -> functionalAreaDto.getFunctionalAreaName().getId()).collect(Collectors.toList()));
        return operationIds.containsAll(List.of(routingDataset.operationClosure.getNaturalId(),
                routingDataset.operationPreliminary.getNaturalId(),
                routingDataset.operationVisual.getNaturalId(),
                routingDataset.operationDimensional.getNaturalId(),
                routingDataset.operationTridimensional.getNaturalId()));
    }

    //---------------- GET Operation Task Header Name ---------------------//

    @Test
    public void getOperationTaskHeaderValues_ok() throws FunctionalException {
        FunctionalAreaHeaderDto operationTaskDto = drtService.getOperationTaskHeader(
                drt.getId(),
                opFADim.getOperation().getNaturalId(),
                opFADim.getFunctionalArea().getNaturalId());
        assertNotNull(operationTaskDto);
        assertEquals(opFADim.getFunctionalArea().getFunctionality().getId(), operationTaskDto.getFunctionality().getId());
    }

    @Test
    public void getOperationTodoListName_ok() throws FunctionalException {
        ListOperationDto listOperationDto = drtService.getOperationsByDrtId(drt.getId());
        OperationFullDto operationPreliminaryDto = listOperationDto.getOperations().stream()
                .filter(operationFullDto -> operationFullDto.getId().equals(routingDataset.operationPreliminary.getNaturalId()))
                .findFirst().get();
        assertEquals(operationPreliminaryDto.getOperationTodoLists().iterator().next().getTodoList().getTodoListName().getId(),
                routingDataset.operationPreliminary.getTodoLists().iterator().next().getTodoListName().getId());
    }

    @Test
    public void getOperationTaskHeader_ko_DrtNotFound() {
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            drtService.getOperationTaskHeader(
                    WRONG_DRT_ID,
                    opFADim.getOperation().getNaturalId(),
                    opFADim.getFunctionalArea().getNaturalId());
        });

        assertEquals("retex.error.drt.not.found", thrown.getMessage());
    }

    @Test
    public void getOperationTaskHeader_ko_RoutingOfDrtNotFound() {
        drt = dataSetInitializer.createDRT(drt1 -> {
            drt1.setChildRequest(dataSetInitializer.createChildRequest(cr -> cr.setRoutingNaturalId(0L)));
            drt1.setStatus(EnumStatus.CREATED);
        });
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            drtService.getOperationTaskHeader(
                    drt.getId(),
                    opFADim.getOperation().getNaturalId(),
                    opFADim.getFunctionalArea().getNaturalId());
        });

        assertEquals("retex.error.drt.routing.not.found", thrown.getMessage());
    }

    @Test
    public void getOperationTaskHeader_ko_OperationFoundNotUnique_NoOperation() {
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            drtService.getOperationTaskHeader(
                    drt.getId(),
                    0L,
                    opFADim.getFunctionalArea().getNaturalId());
        });

        assertEquals("retex.error.drt.operation.found.not.unique", thrown.getMessage());
    }

    @Test
    public void getOperationTaskHeader_ko_OperationFunctionalAreaFoundNotUnique_NoOperation() {
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            drtService.getOperationTaskHeader(
                    drt.getId(),
                    opFADim.getOperation().getNaturalId(),
                    0L);
        });

        assertEquals("retex.error.drt.operation.functional.area.found.not.unique", thrown.getMessage());
    }

    @Test
    public void getOperationTaskHeader_ko_OperationFunctionalAreaFoundNotUnique_MultipleOperations() {
        duplicateOperationFunctionalArea(opFADim.getFunctionalArea());

        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            drtService.getOperationTaskHeader(
                    drt.getId(),
                    opFADim.getOperation().getNaturalId(),
                    opFADim.getFunctionalArea().getNaturalId());
        });
        assertEquals("retex.error.drt.operation.functional.area.found.not.unique", thrown.getMessage());
    }

    @Test
    public void getOperationTaskHeader_ko_OperationTypeNotAllows() {
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            drtService.getOperationTaskHeader(
                    drt.getId(),
                    routingDataset.operationPreliminary.getNaturalId(),
                    opFADim.getFunctionalArea().getNaturalId());
        });

        assertEquals(ConstantExceptionRetex.OPERATION_TYPE_NOT_ALLOW, thrown.getMessage());
    }

    private void duplicateOperationFunctionalArea(FunctionalArea functionalArea) {
        OperationFunctionalArea operationFunctionalAreaDim = dataSetInitializer.createOperationFunctionalArea(opfa -> {
            opfa.setFunctionalArea(functionalArea);
            opfa.setOperation(routingDataset.operationDimensional);
        });
        routingDataset.operationDimensional.addOperationFunctionalArea(operationFunctionalAreaDim);
    }
}
