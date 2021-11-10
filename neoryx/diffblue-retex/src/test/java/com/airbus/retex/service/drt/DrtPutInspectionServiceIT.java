package com.airbus.retex.service.drt;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.drtPicture.DrtPicturesDto;
import com.airbus.retex.business.dto.inspection.InspectionPostValueDto;
import com.airbus.retex.business.dto.inspection.InspectionStepActivationDto;
import com.airbus.retex.business.dto.inspection.InspectionValueDto;
import com.airbus.retex.business.dto.media.MediaDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.dataset.DatasetFactory;
import com.airbus.retex.dataset.DrtDataset;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.control.ControlRoutingComponent;
import com.airbus.retex.model.control.ControlTodoList;
import com.airbus.retex.model.control.ControlVisual;
import com.airbus.retex.model.control.EnumTodoListValue;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.drt.DrtOperationStatusFunctionalArea;
import com.airbus.retex.model.drt.DrtOperationStatusTodoList;
import com.airbus.retex.model.media.MediaTmp;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.drt.DrtRepository;
import com.airbus.retex.service.impl.drt.DrtServiceImpl;


public class DrtPutInspectionServiceIT extends AbstractServiceIT {

    @Autowired
    private DrtServiceImpl drtService;
    @Autowired
    private DrtRepository drtRepository;

    @Autowired
    private DatasetFactory datasetFactory;

    private DrtDataset drtDatasetWithoutControl;
    private DrtDataset drtDataset;
    private InspectionValueDto inspectionValueDto;
    private User user;

    @BeforeEach
    private void before() {
        runInTransaction(() -> {
            //DATASET WITHOUT CONTROL
            drtDatasetWithoutControl = new DrtDataset();
            drtDatasetWithoutControl.requestDataset = datasetFactory.createRequestDataset();
            drtDatasetWithoutControl.drt = dataSetInitializer.createDRT(drt -> {
                drt.setChildRequest(drtDatasetWithoutControl.requestDataset.childRequest);
                drt.setRouting(drtDatasetWithoutControl.requestDataset.routingDataset.routing);
                drt.setStatus(EnumStatus.CREATED);
            });

            //DATASET WITH CONTROL
            drtDataset = datasetFactory.createDrtDataset();

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


        });
        user = dataSetInitializer.createUserWithRole("operateur", "operateur", RoleCode.INTERNAL_OPERATOR);
        dataSetInitializer.createUserFeature(FeatureCode.DRT_INTERNAL_INSPECTION, user, EnumRightLevel.WRITE);

        inspectionValueDto = new InspectionValueDto();
        inspectionValueDto.setTodoListValue(EnumTodoListValue.NOT_OK);
    }

    @Test
    public void shouldThrowFunctionalExceptionValidateDrt() {
        runInTransaction(() -> {
            Drt drt = drtRepository.getOne(drtDataset.drt.getId());
            drt.setStatus(EnumStatus.VALIDATED);
            drtRepository.save(drt);
        });
        assertThrows(FunctionalException.class, () ->
                drtService.putInspectionDetails(drtDataset.drt.getId(),
                        drtDataset.requestDataset.routingDataset.operationPreliminary.getNaturalId(),
                        drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListPreliminary.getNaturalId(), inspectionValueDto, user.getId(), true));
    }

    @Test
    public void shouldThrowFunctionalExceptionWrongUser() {
        runInTransaction(() -> {
            Drt drt = drtRepository.getOne(drtDataset.drt.getId());
            drt.setStatus(EnumStatus.VALIDATED);
            drt.setAssignedOperator(user);
            drtRepository.save(drt);
        });

        User otherOperator = dataSetInitializer.createUserWithRole("operateur2", "operateur2", RoleCode.INTERNAL_OPERATOR);
        dataSetInitializer.createUserFeature(FeatureCode.DRT_INTERNAL_INSPECTION, user, EnumRightLevel.WRITE);
        assertThrows(FunctionalException.class, () ->
                drtService.putInspectionDetails(drtDataset.drt.getId(),
                        drtDataset.requestDataset.routingDataset.operationPreliminary.getNaturalId(),
                        drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListPreliminary.getNaturalId(), inspectionValueDto, otherOperator.getId(), true));
    }

    @Test
    public void shouldThrowFunctionalExceptionWrongRoleUser() {
        runInTransaction(() -> {
            Drt drt = drtRepository.getOne(drtDataset.drt.getId());
            drt.setStatus(EnumStatus.VALIDATED);
            drt.setAssignedOperator(user);
            drtRepository.save(drt);
        });

        User otherOperator = dataSetInitializer.createUserWithRole("TECHNICAL_FILTER", "TECHNICAL_FILTER", RoleCode.TECHNICAL_FILTER);
        dataSetInitializer.createUserFeature(FeatureCode.DRT_INTERNAL_INSPECTION, user, EnumRightLevel.WRITE);
        assertThrows(FunctionalException.class, () ->
                drtService.putInspectionDetails(drtDataset.drt.getId(),
                        drtDataset.requestDataset.routingDataset.operationPreliminary.getNaturalId(),
                        drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListPreliminary.getNaturalId(), inspectionValueDto, otherOperator.getId(), true));
    }


    @Test
    public void shouldPassDrtToInProgress() throws FunctionalException {
        runInTransaction(() -> {
            Drt drt = drtRepository.getOne(drtDataset.drt.getId());
            drt.setStatus(EnumStatus.CREATED);
            drtRepository.save(drt);
        });
        drtService.putInspectionDetails(drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationPreliminary.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListPreliminary.getNaturalId(), inspectionValueDto, user.getId(), true);
        Drt drt = drtService.findDrtById(drtDataset.drt.getId());
        assertThat(drt.getStatus(), equalTo(EnumStatus.IN_PROGRESS));
    }

    @Test
    public void shouldUpdateValueTodoList() throws FunctionalException {
        runInTransaction(() -> {
            try {
                drtService.putInspectionDetails(drtDataset.drt.getId(),
                        drtDataset.requestDataset.routingDataset.operationPreliminary.getNaturalId(),
                        drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListPreliminary.getNaturalId(), inspectionValueDto, user.getId(), true);
            } catch (FunctionalException e) {
                Assertions.fail("functional error");
            }
            Drt drt = null;
            try {
                drt = drtService.findDrtById(drtDataset.drt.getId());
            } catch (NotFoundException e) {
                Assertions.fail("drt not found");
            }
            assertThat(drt.getControls().stream().filter(control -> control.getId().equals(drtDataset.controlPremiminary.getId())).findFirst().get().getValue(), equalTo(inspectionValueDto.getTodoListValue()));

            assertThat(drt.getOperationStatus().stream()
                            .filter(abstractDrtOperationStatus ->  abstractDrtOperationStatus instanceof DrtOperationStatusTodoList)
                            .map(abstractDrtOperationStatus -> (DrtOperationStatusTodoList) abstractDrtOperationStatus)
                            .filter(drtOperationStatusTodoList -> drtOperationStatusTodoList.getTodoList().getNaturalId()
                                    .equals(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListPreliminary.getNaturalId()))
                            .filter(drtOperationStatusTodoList -> drtOperationStatusTodoList.getOperation().getNaturalId()
                                    .equals(drtDataset.requestDataset.routingDataset.operationPreliminary.getNaturalId())).findFirst().get().getStatus(),
                    equalTo(EnumStatus.VALIDATED));
        });
    }

    @Test
    public void shouldInsertValueTodoList() throws FunctionalException {
        runInTransaction(() -> {
            try {
                drtService.putInspectionDetails(drtDatasetWithoutControl.drt.getId(),
                        drtDatasetWithoutControl.requestDataset.routingDataset.operationPreliminary.getNaturalId(),
                        drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListPreliminary.getNaturalId(), inspectionValueDto, user.getId(), true);
            } catch (FunctionalException e) {
                Assertions.fail("functional error");
            }
            Drt drt = null;
            try {
                drt = drtService.findDrtById(drtDatasetWithoutControl.drt.getId());
            } catch (NotFoundException e) {
                Assertions.fail("drt not found");
            }
            assertThat(drt.getControls().stream()
                            .filter(control -> control instanceof ControlTodoList)
                            .map(control -> (ControlTodoList) control)
                            .filter(controlTodoList -> controlTodoList.getOperation().getNaturalId().equals(drtDatasetWithoutControl.requestDataset.routingDataset.operationPreliminary.getNaturalId()))
                            .filter(control -> control.getTodoList().getNaturalId().equals(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListPreliminary.getNaturalId()))
                            .findFirst().get().getValue()
                    , equalTo(inspectionValueDto.getTodoListValue()));

            assertThat(drt.getOperationStatus().stream()
                            .filter(abstractDrtOperationStatus ->  abstractDrtOperationStatus instanceof DrtOperationStatusTodoList)
                            .map(abstractDrtOperationStatus -> (DrtOperationStatusTodoList) abstractDrtOperationStatus)
                            .filter(drtOperationStatusTodoList -> drtOperationStatusTodoList.getTodoList().getNaturalId()
                                    .equals(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListPreliminary.getNaturalId()))
                            .filter(drtOperationStatusTodoList -> drtOperationStatusTodoList.getOperation().getNaturalId()
                                    .equals(drtDatasetWithoutControl.requestDataset.routingDataset.operationPreliminary.getNaturalId())).findFirst().get().getStatus(),
                    equalTo(EnumStatus.VALIDATED));
        });

    }

    @Test
    public void shouldUpdateValueDimensional() throws FunctionalException {
        InspectionPostValueDto inspectionPostValueDto = new InspectionPostValueDto();
        inspectionPostValueDto.setPostThresholdId(drtDataset.requestDataset.routingDataset.routingFunctionalAreaPostDimensional.getNaturalId());
        inspectionPostValueDto.setValue(54F);
        InspectionStepActivationDto inspectionStepActivationDto = new InspectionStepActivationDto();
        inspectionStepActivationDto.setPosts(List.of(inspectionPostValueDto));
        inspectionStepActivationDto.setId(drtDataset.requestDataset.routingDataset.stepActivationDimensional.getNaturalId());
        inspectionStepActivationDto.setIdRoutingComponentIndex(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexDimensional.getNaturalId());

        inspectionValueDto.setSteps(List.of(inspectionStepActivationDto));

        runInTransaction(() -> {
            try {
                drtService.putInspectionDetails(drtDataset.drt.getId(),
                        drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                        drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId(), inspectionValueDto, user.getId(), true);
            } catch (FunctionalException e) {
                Assertions.fail("functional error");
            }
            Drt drt = null;
            try {
                drt = drtService.findDrtById(drtDataset.drt.getId());
            } catch (NotFoundException e) {
                Assertions.fail("drt not found");
            }
            assertThat(drt.getControls().stream()
                            .filter(control -> control.getId().equals(drtDataset.controlDimensional.getId())).findFirst().get().getValue(),
                    equalTo(inspectionValueDto.getSteps().get(0).getPosts().get(0).getValue()));
            assertThat(drt.getOperationStatus().stream()
                            .filter(abstractDrtOperationStatus -> abstractDrtOperationStatus instanceof DrtOperationStatusFunctionalArea)
                            .map(abstractDrtOperationStatus -> (DrtOperationStatusFunctionalArea) abstractDrtOperationStatus)
                            .filter(drtOperationStatusFunctionalArea -> drtOperationStatusFunctionalArea.getOperationFunctionalArea().getNaturalId()
                                    .equals(drtDataset.requestDataset.routingDataset.operationFunctionalAreaDimensional.getNaturalId())).findFirst().get().getStatus(),
                    equalTo(EnumStatus.Q_CHECK));
        });
    }

    //RC
    @Test
    public void shouldInsertValueDimensional() {
        InspectionPostValueDto inspectionPostValueDto = new InspectionPostValueDto();
        inspectionPostValueDto.setPostThresholdId(drtDatasetWithoutControl.requestDataset.routingDataset.routingFunctionalAreaPostDimensional.getNaturalId());
        inspectionPostValueDto.setValue(54F);
        InspectionStepActivationDto inspectionStepActivationDto = new InspectionStepActivationDto();
        inspectionStepActivationDto.setPosts(List.of(inspectionPostValueDto));
        inspectionStepActivationDto.setId(drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getNaturalId());
        inspectionStepActivationDto.setIdRoutingComponentIndex(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexDimensional.getNaturalId());
        inspectionValueDto.setSteps(List.of(inspectionStepActivationDto));

        runInTransaction(() -> {
            try {
                drtService.putInspectionDetails(drtDatasetWithoutControl.drt.getId(),
                        drtDatasetWithoutControl.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                        drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId(), inspectionValueDto, user.getId(), true);
            } catch (FunctionalException e) {
                Assertions.fail("functional error");
            }
            Drt drt = null;
            try {
                drt = drtService.findDrtById(drtDatasetWithoutControl.drt.getId());
            } catch (NotFoundException e) {
                Assertions.fail("drt not found");
            }

            assertThat(drt.getControls().stream()
                            .filter(control -> control instanceof ControlRoutingComponent)
                            .map(control -> (ControlRoutingComponent) control)
                            .filter(controlRoutingComponent -> controlRoutingComponent.getRoutingFunctionalAreaPost().getNaturalId()
                                    .equals(drtDatasetWithoutControl.requestDataset.routingDataset.routingFunctionalAreaPostDimensional.getNaturalId()))
                            .findFirst().get().getValue()
                    , equalTo(inspectionValueDto.getSteps().get(0).getPosts().get(0).getValue()));
            assertThat(drt.getOperationStatus().stream()
                            .filter(abstractDrtOperationStatus -> abstractDrtOperationStatus instanceof DrtOperationStatusFunctionalArea)
                            .map(abstractDrtOperationStatus -> (DrtOperationStatusFunctionalArea) abstractDrtOperationStatus)
                            .filter(drtOperationStatusFunctionalArea -> drtOperationStatusFunctionalArea.getOperationFunctionalArea().getNaturalId()
                                    .equals(drtDatasetWithoutControl.requestDataset.routingDataset.operationFunctionalAreaDimensional.getNaturalId())).findFirst().get().getStatus(),
                    equalTo(EnumStatus.Q_CHECK));
        });
    }

    @Test
    public void shouldUpdateValueVisual() throws FunctionalException {
        InspectionPostValueDto inspectionPostValueDto = new InspectionPostValueDto();
        inspectionPostValueDto.setPostThresholdId(drtDataset.requestDataset.routingDataset.routingFunctionalAreaPostVisual.getNaturalId());
        inspectionPostValueDto.setValue(Boolean.FALSE);
        InspectionStepActivationDto inspectionStepActivationDto = new InspectionStepActivationDto();
        inspectionStepActivationDto.setPosts(List.of(inspectionPostValueDto));
        inspectionStepActivationDto.setId(drtDataset.requestDataset.routingDataset.stepActivationVisual.getNaturalId());
        inspectionStepActivationDto.setIdRoutingComponentIndex(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexVisual.getNaturalId());

        inspectionValueDto.setSteps(List.of(inspectionStepActivationDto));

        runInTransaction(() -> {
            try {
                drtService.putInspectionDetails(drtDataset.drt.getId(),
                        drtDataset.requestDataset.routingDataset.operationVisual.getNaturalId(),
                        drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId(), inspectionValueDto, user.getId(), true);
            } catch (FunctionalException e) {
                Assertions.fail("functional error");
            }
            Drt drt = null;
            try {
                drt = drtService.findDrtById(drtDataset.drt.getId());
            } catch (NotFoundException e) {
                Assertions.fail("drt not found");
            }
            assertThat(drt.getControls().stream()
                            .filter(control -> control.getId().equals(drtDataset.controlVisual.getId())).findFirst().get().getValue(),
                    equalTo(inspectionValueDto.getSteps().get(0).getPosts().get(0).getValue()));
            assertThat(drt.getOperationStatus().stream()
                            .filter(abstractDrtOperationStatus -> abstractDrtOperationStatus instanceof DrtOperationStatusFunctionalArea)
                            .map(abstractDrtOperationStatus -> (DrtOperationStatusFunctionalArea) abstractDrtOperationStatus)
                            .filter(drtOperationStatusFunctionalArea -> drtOperationStatusFunctionalArea.getOperationFunctionalArea().getNaturalId()
                                    .equals(drtDataset.requestDataset.routingDataset.operationFunctionalAreaVisual.getNaturalId())).findFirst().get().getStatus(),
                    equalTo(EnumStatus.VALIDATED));

        });
    }

    //RC
    @Test
    public void shouldInsertValueVisual() throws FunctionalException {
        InspectionPostValueDto inspectionPostValueDto = new InspectionPostValueDto();
        inspectionPostValueDto.setPostThresholdId(drtDatasetWithoutControl.requestDataset.routingDataset.routingFunctionalAreaPostVisual.getNaturalId());
        inspectionPostValueDto.setValue(Boolean.FALSE);
        InspectionStepActivationDto inspectionStepActivationDto = new InspectionStepActivationDto();
        inspectionStepActivationDto.setPosts(List.of(inspectionPostValueDto));
        DrtPicturesDto drtPicturesDto = new DrtPicturesDto();
        inspectionStepActivationDto.setId(drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationVisual.getNaturalId());
        inspectionStepActivationDto.setIdRoutingComponentIndex(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexDimensional.getNaturalId());

        inspectionStepActivationDto.setDrtPictures(drtPicturesDto);
        inspectionValueDto.setSteps(List.of(inspectionStepActivationDto));

        runInTransaction(() -> {
            try {
                drtService.putInspectionDetails(drtDatasetWithoutControl.drt.getId(),
                        drtDatasetWithoutControl.requestDataset.routingDataset.operationVisual.getNaturalId(),
                        drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId(), inspectionValueDto, user.getId(), true);
            } catch (FunctionalException e) {
                Assertions.fail(e.getMessage());
            }
            Drt drt = null;
            try {
                drt = drtService.findDrtById(drtDatasetWithoutControl.drt.getId());
            } catch (NotFoundException e) {
                Assertions.fail(e.getMessage());
            }

            assertThat(drt.getControls().stream()
                            .filter(control -> control instanceof ControlVisual)
                            .map(control -> (ControlVisual) control)
                            .filter(controlVisual -> controlVisual.getRoutingFunctionalAreaPost().getNaturalId()
                                    .equals(drtDatasetWithoutControl.requestDataset.routingDataset.routingFunctionalAreaPostVisual.getNaturalId()))
                            .findFirst().get().getValue()
                    , equalTo(inspectionValueDto.getSteps().get(0).getPosts().get(0).getValue()));

            assertThat(drt.getOperationStatus().stream()
                            .filter(abstractDrtOperationStatus ->  abstractDrtOperationStatus instanceof DrtOperationStatusFunctionalArea)
                            .map(abstractDrtOperationStatus -> (DrtOperationStatusFunctionalArea) abstractDrtOperationStatus)
                            .filter(drtOperationStatusFunctionalArea -> drtOperationStatusFunctionalArea.getOperationFunctionalArea().getNaturalId()
                                    .equals(drtDatasetWithoutControl.requestDataset.routingDataset.operationFunctionalAreaVisual.getNaturalId())).findFirst().get().getStatus(),
                    equalTo(EnumStatus.VALIDATED));

        });
    }

    @Test
    public void putRC_AddDrtPictures_ok() {
        MediaTmp mediaTmp = dataSetInitializer.createTemporaryMedia();
        MediaDto mediaDto = new MediaDto();
        mediaDto.setFilename(mediaTmp.getFilename());
        mediaDto.setUuid(mediaTmp.getUuid());
        mediaDto.setIsFromThingworx(false);
        DrtPicturesDto drtPicturesDto = new DrtPicturesDto();
        drtPicturesDto.setActivated(drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getActivated());
        drtPicturesDto.setImages(Set.of(mediaDto));
        InspectionStepActivationDto inspectionStepActivationDto = new InspectionStepActivationDto();
        inspectionStepActivationDto.setDrtPictures(drtPicturesDto);
        inspectionStepActivationDto.setId(drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getNaturalId());
        inspectionStepActivationDto.setIdRoutingComponentIndex(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexDimensional.getNaturalId());

        inspectionValueDto.setSteps(List.of(inspectionStepActivationDto));

        runInTransaction(() -> {
            try {
                drtService.putInspectionDetails(drtDatasetWithoutControl.drt.getId(),
                        drtDatasetWithoutControl.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                        drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId(), inspectionValueDto, user.getId(), false);

                Drt drt = drtService.findDrtById(drtDatasetWithoutControl.drt.getId());
                assertFalse(drt.getDrtPictures().isEmpty());
                assertEquals(drt.getDrtPictures().iterator().next().getStepActivation().getNaturalId(), drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getNaturalId());
                assertEquals(drt.getDrtPictures().iterator().next().getStepActivation().getActivated(), drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getActivated());
                assertEquals(drt.getDrtPictures().iterator().next().getMedias().iterator().next().getUuid(), mediaTmp.getUuid());
            } catch (FunctionalException e) {
                Assertions.fail(e.getMessage());
            }
        });
    }


    @Test
    public void putRC_UpdateDrtPicturesMedias_ok() {
        MediaTmp mediaTmp = dataSetInitializer.createTemporaryMedia();
        final UUID mediaUuid = mediaTmp.getUuid();
        MediaDto mediaDto = new MediaDto();
        mediaDto.setFilename(mediaTmp.getFilename());
        mediaDto.setUuid(mediaUuid);
        mediaDto.setIsFromThingworx(false);
        DrtPicturesDto drtPicturesDto = new DrtPicturesDto();
        drtPicturesDto.setActivated(drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getActivated());
        drtPicturesDto.setImages(Set.of(mediaDto));
        InspectionStepActivationDto inspectionStepActivationDto = new InspectionStepActivationDto();
        inspectionStepActivationDto.setDrtPictures(drtPicturesDto);
        inspectionStepActivationDto.setId(drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getNaturalId());
        inspectionStepActivationDto.setIdRoutingComponentIndex(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexDimensional.getNaturalId());
        inspectionValueDto.setSteps(List.of(inspectionStepActivationDto));

        runInTransaction(() -> {
            try {
                drtService.putInspectionDetails(drtDatasetWithoutControl.drt.getId(),
                        drtDatasetWithoutControl.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                        drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId(), inspectionValueDto, user.getId(), false);

                Drt drt = drtService.findDrtById(drtDatasetWithoutControl.drt.getId());
                drtPicturesDto.setId(drt.getDrtPictures().iterator().next().getId());

                assertFalse(drt.getDrtPictures().isEmpty());
                assertEquals(1, drt.getDrtPictures().size());
                assertEquals(drt.getDrtPictures().iterator().next().getStepActivation().getNaturalId(), drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getNaturalId());
                assertEquals(drt.getDrtPictures().iterator().next().getStepActivation().getActivated(), drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getActivated());
                assertEquals(drt.getDrtPictures().iterator().next().getMedias().iterator().next().getUuid(), mediaUuid);
            } catch (FunctionalException e) {
                Assertions.fail(e.getMessage());
            }
        });
        mediaTmp = dataSetInitializer.createTemporaryMedia();
        mediaDto.setFilename(mediaTmp.getFilename());
        mediaDto.setUuid(mediaTmp.getUuid());
        drtPicturesDto.setImages(Set.of(mediaDto));
        final UUID newMediaUuid = mediaTmp.getUuid();
        inspectionStepActivationDto = new InspectionStepActivationDto();
        inspectionStepActivationDto.setDrtPictures(drtPicturesDto);
        inspectionStepActivationDto.setId(drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getNaturalId());
        inspectionValueDto.setSteps(List.of(inspectionStepActivationDto));

        runInTransaction(() -> {
            try {
                drtService.putInspectionDetails(drtDatasetWithoutControl.drt.getId(),
                        drtDatasetWithoutControl.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                        drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId(), inspectionValueDto, user.getId(), false);

                Drt drt = drtService.findDrtById(drtDatasetWithoutControl.drt.getId());
                assertFalse(drt.getDrtPictures().isEmpty());
                assertEquals(1, drt.getDrtPictures().size());
                assertEquals(drt.getDrtPictures().iterator().next().getStepActivation().getNaturalId(), drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getNaturalId());
                assertEquals(drt.getDrtPictures().iterator().next().getStepActivation().getActivated(), drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getActivated());
                assertEquals(drt.getDrtPictures().iterator().next().getMedias().iterator().next().getUuid(), newMediaUuid);
            } catch (FunctionalException e) {
                Assertions.fail(e.getMessage());
            }
        });
    }


    @Test
    public void putRC_RemoveDrtPicturesMedias_ok() {
        MediaTmp mediaTmp = dataSetInitializer.createTemporaryMedia();
        final UUID mediaUuid = mediaTmp.getUuid();
        MediaDto mediaDto = new MediaDto();
        mediaDto.setFilename(mediaTmp.getFilename());
        mediaDto.setUuid(mediaUuid);
        mediaDto.setIsFromThingworx(false);
        DrtPicturesDto drtPicturesDto = new DrtPicturesDto();
        drtPicturesDto.setActivated(drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getActivated());
        drtPicturesDto.setImages(Set.of(mediaDto));
        InspectionStepActivationDto inspectionStepActivationDto = new InspectionStepActivationDto();
        inspectionStepActivationDto.setDrtPictures(drtPicturesDto);
        inspectionStepActivationDto.setId(drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getNaturalId());
        inspectionStepActivationDto.setIdRoutingComponentIndex(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexDimensional.getNaturalId());

        inspectionValueDto.setSteps(List.of(inspectionStepActivationDto));
        runInTransaction(() -> {
            try {
                drtService.putInspectionDetails(drtDatasetWithoutControl.drt.getId(),
                        drtDatasetWithoutControl.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                        drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId(), inspectionValueDto, user.getId(), false);

                Drt drt = drtService.findDrtById(drtDatasetWithoutControl.drt.getId());
                drtPicturesDto.setId(drt.getDrtPictures().iterator().next().getId());

                assertFalse(drt.getDrtPictures().isEmpty());
                assertEquals(1, drt.getDrtPictures().size());
                assertEquals(drt.getDrtPictures().iterator().next().getStepActivation().getNaturalId(), drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getNaturalId());
                assertEquals(drt.getDrtPictures().iterator().next().getStepActivation().getActivated(), drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getActivated());
                assertEquals(drt.getDrtPictures().iterator().next().getMedias().iterator().next().getUuid(), mediaUuid);
            } catch (FunctionalException e) {
                Assertions.fail(e.getMessage());
            }
        });

        drtPicturesDto.setImages(Set.of());
        inspectionStepActivationDto = new InspectionStepActivationDto();
        inspectionStepActivationDto.setDrtPictures(drtPicturesDto);
        inspectionStepActivationDto.setId(drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getNaturalId());
        inspectionValueDto.setSteps(List.of(inspectionStepActivationDto));

        runInTransaction(() -> {
            try {
                drtService.putInspectionDetails(drtDatasetWithoutControl.drt.getId(),
                        drtDatasetWithoutControl.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                        drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId(), inspectionValueDto, user.getId(), false);

                Drt drt = drtService.findDrtById(drtDatasetWithoutControl.drt.getId());
                assertFalse(drt.getDrtPictures().isEmpty());
                assertEquals(1, drt.getDrtPictures().size());
                assertEquals(drt.getDrtPictures().iterator().next().getStepActivation().getNaturalId(), drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getNaturalId());
                assertEquals(drt.getDrtPictures().iterator().next().getStepActivation().getActivated(), drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getActivated());
                assertTrue(drt.getDrtPictures().iterator().next().getMedias().isEmpty());
            } catch (FunctionalException e) {
                Assertions.fail(e.getMessage());
            }
        });
    }

    @Test
    public void putRC_AddDrtPictures_ko_StepActivationNotFound() throws FunctionalException {
        MediaTmp mediaTmp = dataSetInitializer.createTemporaryMedia();
        MediaDto mediaDto = new MediaDto();
        mediaDto.setFilename(mediaTmp.getFilename());
        mediaDto.setUuid(mediaTmp.getUuid());
        mediaDto.setIsFromThingworx(false);
        DrtPicturesDto drtPicturesDto = new DrtPicturesDto();
        drtPicturesDto.setActivated(drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getActivated());
        drtPicturesDto.setImages(Set.of(mediaDto));
        InspectionStepActivationDto inspectionStepActivationDto = new InspectionStepActivationDto();
        inspectionStepActivationDto.setDrtPictures(drtPicturesDto);
        inspectionStepActivationDto.setId(0L);
        inspectionStepActivationDto.setIdRoutingComponentIndex(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexDimensional.getNaturalId());

        inspectionValueDto.setSteps(List.of(inspectionStepActivationDto));
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            drtService.putInspectionDetails(drtDatasetWithoutControl.drt.getId(),
                    drtDatasetWithoutControl.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                    drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId(), inspectionValueDto, user.getId(), false);
        });
        assertEquals("retex.stepActivation.not.found", thrown.getMessage());
    }

    @Test
    public void shouldPassQcheckToNull() {
        InspectionStepActivationDto inspectionStepActivationDto = new InspectionStepActivationDto();
        inspectionStepActivationDto.setId(drtDataset.requestDataset.routingDataset.stepActivationTridimensional.getNaturalId());
        inspectionStepActivationDto.setIdRoutingComponentIndex(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexTridimensional.getNaturalId());

        inspectionValueDto.setSteps(List.of(inspectionStepActivationDto));

        runInTransaction(() -> {
            try {
                drtService.putInspectionDetails(drtDataset.drt.getId(),
                        drtDataset.requestDataset.routingDataset.operationTridimensional.getNaturalId(),
                        drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId(), inspectionValueDto, user.getId(), true);
            } catch (FunctionalException e) {
                Assertions.fail("functional error");
            }
            Drt drt = null;
            try {
                drt = drtService.findDrtById(drtDataset.drt.getId());
            } catch (NotFoundException e) {
                Assertions.fail("drt not found");
            }
            assertThat(drt.getQCheckRoutingComponents().stream()
                    .filter(qcheckRoutingComponent -> qcheckRoutingComponent.getRoutingComponentIndex().getNaturalId().equals(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexTridimensional.getNaturalId()))
                    .filter(qcheckRoutingComponent -> qcheckRoutingComponent.getOperationFunctionalArea().getNaturalId().equals(drtDataset.requestDataset.routingDataset.operationFunctionalAreaTridimensional.getNaturalId()))
                    .findFirst().get().getValue(), equalTo(null));

        });
    }
}
