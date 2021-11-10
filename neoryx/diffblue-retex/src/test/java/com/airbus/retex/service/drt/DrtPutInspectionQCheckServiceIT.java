package com.airbus.retex.service.drt;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.inspection.InspectionQCheckValueDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.dataset.DatasetFactory;
import com.airbus.retex.dataset.DrtDataset;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.drt.DrtOperationStatusFunctionalArea;
import com.airbus.retex.model.user.User;
import com.airbus.retex.service.impl.drt.DrtQCheckServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class DrtPutInspectionQCheckServiceIT extends AbstractServiceIT {

    @Autowired
    private DatasetFactory datasetFactory;

    @Autowired
    private IDrtQCheckService drtQCheckService;
    @Autowired
    private IDrtService drtService;

    private DrtDataset drtDataset;
    private List<InspectionQCheckValueDto> inspectionQCheckValueDtoList;

    private User user;

    @BeforeEach
    private void before() {
        runInTransaction(() -> {
            //DATASET WITH CONTROL
            drtDataset = datasetFactory.createDrtDataset();
            drtDataset.drtOperationStatusDimensional.setStatus(EnumStatus.Q_CHECK);
            drtDataset.drtOperationStatusLaboratory.setStatus(EnumStatus.Q_CHECK);
            drtDataset.drtOperationStatusTriDimensional.setStatus(EnumStatus.Q_CHECK);

            //create qcheck already True
            drtDataset.qcheckRoutingLaboratory = dataSetInitializer.createQcheckRoutingComponent(qcheckRoutingComponent -> {
                qcheckRoutingComponent.setDrt(drtDataset.drt);
                qcheckRoutingComponent.setOperationFunctionalArea(drtDataset.requestDataset.routingDataset.operationFunctionalAreaLaboratory);
                qcheckRoutingComponent.setRoutingComponentIndex(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexLaboratory);
                qcheckRoutingComponent.setValue(Boolean.TRUE);
            });

            //create qcheck already false
            drtDataset.qcheckRoutingTriDimensional = dataSetInitializer.createQcheckRoutingComponent(qcheckRoutingComponent -> {
                qcheckRoutingComponent.setDrt(drtDataset.drt);
                qcheckRoutingComponent.setOperationFunctionalArea(drtDataset.requestDataset.routingDataset.operationFunctionalAreaTridimensional);
                qcheckRoutingComponent.setRoutingComponentIndex(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexTridimensional);
                qcheckRoutingComponent.setValue(Boolean.FALSE);
            });

            user = dataSetInitializer.createUserWithRole("QUALITY_CONTROLLER", "QUALITY_CONTROLLER", RoleCode.QUALITY_CONTROLLER);
            dataSetInitializer.createUserFeature(FeatureCode.DRT_QUALITY_CHECK, user, EnumRightLevel.WRITE);
        });


    }

    @Test
    public void qCheckOkshoulValidateTask() throws FunctionalException {  // Vérifier tache passe en validate
        InspectionQCheckValueDto inspectionQCheckValueDto = new InspectionQCheckValueDto();
        inspectionQCheckValueDto.setIdRoutingComponentIndex(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexDimensional.getNaturalId());
        inspectionQCheckValueDto.setQCheckValue(true);
        inspectionQCheckValueDtoList = List.of(inspectionQCheckValueDto);

        runInTransaction(() -> {
            try {
                drtQCheckService.putInspectionQCheck(drtDataset.drt.getId(),
                        drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                        drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId(),
                        inspectionQCheckValueDtoList,
                        user.getId(), true);
            } catch (FunctionalException e) {
                Assertions.fail("functional error");
            }
            Drt drt = null;
            try {
                drt = drtService.findDrtById(drtDataset.drt.getId());
            } catch (NotFoundException e) {
                Assertions.fail("drt not found");
            }
            assertThat(drt.getOperationStatus().stream().filter(abstractDrtOperationStatus -> abstractDrtOperationStatus instanceof DrtOperationStatusFunctionalArea)
                    .map(abstractDrtOperationStatus -> (DrtOperationStatusFunctionalArea) abstractDrtOperationStatus)
                    .filter(drtOperationStatusFunctionalArea -> drtOperationStatusFunctionalArea.getOperationFunctionalArea().getNaturalId().equals(drtDataset.requestDataset.routingDataset.operationFunctionalAreaDimensional.getNaturalId()))
                    .findFirst().get().getStatus(), equalTo(EnumStatus.VALIDATED));

        });
    }

    @Test
    public void qCheckOkshoulValidateTaskAlreadyFalse() throws FunctionalException {  // Vérifier tache passe en validate
        InspectionQCheckValueDto inspectionQCheckValueDto = new InspectionQCheckValueDto();
        inspectionQCheckValueDto.setIdRoutingComponentIndex(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexTridimensional.getNaturalId());
        inspectionQCheckValueDto.setQCheckValue(true);
        inspectionQCheckValueDtoList = List.of(inspectionQCheckValueDto);

        runInTransaction(() -> {
            try {
                drtQCheckService.putInspectionQCheck(drtDataset.drt.getId(),
                        drtDataset.requestDataset.routingDataset.operationTridimensional.getNaturalId(),
                        drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId(),
                        inspectionQCheckValueDtoList,
                        user.getId(), true);
            } catch (FunctionalException e) {
                Assertions.fail("functional error");
            }
            Drt drt = null;
            try {
                drt = drtService.findDrtById(drtDataset.drt.getId());
            } catch (NotFoundException e) {
                Assertions.fail("drt not found");
            }
            assertThat(drt.getOperationStatus().stream().filter(abstractDrtOperationStatus -> abstractDrtOperationStatus instanceof DrtOperationStatusFunctionalArea)
                    .map(abstractDrtOperationStatus -> (DrtOperationStatusFunctionalArea) abstractDrtOperationStatus)
                    .filter(drtOperationStatusFunctionalArea -> drtOperationStatusFunctionalArea.getOperationFunctionalArea().getNaturalId().equals(drtDataset.requestDataset.routingDataset.operationFunctionalAreaTridimensional.getNaturalId()))
                    .findFirst().get().getStatus(), equalTo(EnumStatus.VALIDATED));

        });
    }

    @Test
    public void qCheckNotOkShouldPassInProgressTask() throws FunctionalException {
        InspectionQCheckValueDto inspectionQCheckValueDto = new InspectionQCheckValueDto();
        inspectionQCheckValueDto.setIdRoutingComponentIndex(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexDimensional.getNaturalId());
        inspectionQCheckValueDto.setQCheckValue(false);
        inspectionQCheckValueDtoList = List.of(inspectionQCheckValueDto);

        runInTransaction(() -> {
            try {
                drtQCheckService.putInspectionQCheck(drtDataset.drt.getId(),
                        drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                        drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId(),
                        inspectionQCheckValueDtoList,
                        user.getId(), true);
            } catch (FunctionalException e) {
                Assertions.fail("functional error");
            }
            Drt drt = null;
            try {
                drt = drtService.findDrtById(drtDataset.drt.getId());
            } catch (NotFoundException e) {
                Assertions.fail("drt not found");
            }
            assertThat(drt.getOperationStatus().stream().filter(abstractDrtOperationStatus -> abstractDrtOperationStatus instanceof DrtOperationStatusFunctionalArea)
                    .map(abstractDrtOperationStatus -> (DrtOperationStatusFunctionalArea) abstractDrtOperationStatus)
                    .filter(drtOperationStatusFunctionalArea -> drtOperationStatusFunctionalArea.getOperationFunctionalArea().getNaturalId().equals(drtDataset.requestDataset.routingDataset.operationFunctionalAreaDimensional.getNaturalId()))
                    .findFirst().get().getStatus(), equalTo(EnumStatus.IN_PROGRESS));
        });
    }

    @Test
    public void qCheckNotValidateOkShouldStayInQcheckStatus() throws FunctionalException {
        InspectionQCheckValueDto inspectionQCheckValueDto = new InspectionQCheckValueDto();
        inspectionQCheckValueDto.setIdRoutingComponentIndex(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexDimensional.getNaturalId());
        inspectionQCheckValueDto.setQCheckValue(false);
        inspectionQCheckValueDtoList = List.of(inspectionQCheckValueDto);

        runInTransaction(() -> {
            try {
                drtQCheckService.putInspectionQCheck(drtDataset.drt.getId(),
                        drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                        drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId(),
                        inspectionQCheckValueDtoList,
                        user.getId(), false);
            } catch (FunctionalException e) {
                Assertions.fail("functional error");
            }
            Drt drt = null;
            try {
                drt = drtService.findDrtById(drtDataset.drt.getId());
            } catch (NotFoundException e) {
                Assertions.fail("drt not found");
            }
            assertThat(drt.getOperationStatus().stream().filter(abstractDrtOperationStatus -> abstractDrtOperationStatus instanceof DrtOperationStatusFunctionalArea)
                    .map(abstractDrtOperationStatus -> (DrtOperationStatusFunctionalArea) abstractDrtOperationStatus)
                    .filter(drtOperationStatusFunctionalArea -> drtOperationStatusFunctionalArea.getOperationFunctionalArea().getNaturalId().equals(drtDataset.requestDataset.routingDataset.operationFunctionalAreaDimensional.getNaturalId()))
                    .findFirst().get().getStatus(), equalTo(EnumStatus.Q_CHECK));
        });
    }

    @Test
    public void  shouldThrowExceptionAllQcheckMustBeDone(){ // vérifier tout les qcheck doivent être rempli
        InspectionQCheckValueDto inspectionQCheckValueDto = new InspectionQCheckValueDto();
        inspectionQCheckValueDtoList = List.of(inspectionQCheckValueDto);

        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            drtQCheckService.putInspectionQCheck(drtDataset.drt.getId(),
                    drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                    drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId(),
                    inspectionQCheckValueDtoList,
                    user.getId(), true);
        });

        assertEquals("retex.error.drt.qcheck.missing", thrown.getMessage());
    }
}
