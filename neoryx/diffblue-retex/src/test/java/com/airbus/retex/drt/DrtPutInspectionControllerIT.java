package com.airbus.retex.drt;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.inspection.InspectionPostValueDto;
import com.airbus.retex.business.dto.inspection.InspectionStepActivationDto;
import com.airbus.retex.business.dto.inspection.InspectionValueDto;
import com.airbus.retex.dataset.DatasetFactory;
import com.airbus.retex.dataset.DrtDataset;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.control.EnumTodoListValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class DrtPutInspectionControllerIT extends BaseControllerTest {

    private static final String API_DRT = "/api/drts/";
    private static final String API_DRT_INSPECTION = API_DRT + "{id}/operations/{operationId}/tasks/{taskId}";

    @Autowired
    private DatasetFactory datasetFactory;

    private DrtDataset drtDatasetWithoutControl;
    private DrtDataset drtDataset;
    private InspectionValueDto inspectionValueDto;

    @BeforeEach
    private void before() {
        runInTransaction(() -> {
            //DATASET WITHOUT CONTROL
            drtDatasetWithoutControl = new DrtDataset();
            drtDatasetWithoutControl.requestDataset = datasetFactory.createRequestDataset();
            drtDatasetWithoutControl.drt = dataSetInitializer.createDRT(drt -> {
                drt.setChildRequest(drtDatasetWithoutControl.requestDataset.childRequest);
                drt.setRouting(drtDatasetWithoutControl.requestDataset.routingDataset.routing);
                drt.setStatus(EnumStatus.IN_PROGRESS);
            });

            //DATASET WITH CONTROL
            drtDataset = datasetFactory.createDrtDataset();
        });
        asUser = dataSetInitializer.createUserWithRole("operateur", "operateur", RoleCode.INTERNAL_OPERATOR);
        dataSetInitializer.createUserFeature(FeatureCode.DRT_INTERNAL_INSPECTION, asUser, EnumRightLevel.WRITE);

        inspectionValueDto = new InspectionValueDto();
        inspectionValueDto.setTodoListValue(EnumTodoListValue.OK);
    }


    @Test
    public void putDrtInspectionWriteLevelOk() throws Exception {
        InspectionPostValueDto inspectionPostValueDto = new InspectionPostValueDto();
        inspectionPostValueDto.setPostThresholdId(drtDataset.requestDataset.routingDataset.routingFunctionalAreaPostDimensional.getNaturalId());
        inspectionPostValueDto.setValue(54F);

        InspectionStepActivationDto inspectionStepActivationDto = new InspectionStepActivationDto();
        inspectionStepActivationDto.setPosts(List.of(inspectionPostValueDto));
        inspectionStepActivationDto.setId(drtDataset.requestDataset.routingDataset.stepActivationDimensional.getNaturalId());
        inspectionValueDto.setSteps(List.of(inspectionStepActivationDto));

        withRequest = put(API_DRT_INSPECTION,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId())
                .content(objectMapper.writeValueAsBytes(inspectionValueDto));

        expectedStatus = HttpStatus.NO_CONTENT;
        abstractCheck();
    }

    @Test
    public void putDrtInspectionReadLevelKo() throws Exception {
        dataSetInitializer.createUserFeature(FeatureCode.DRT_INTERNAL_INSPECTION, dataset.user_simpleUser, EnumRightLevel.READ);
        asUser = dataset.user_simpleUser;

        withRequest = put(API_DRT_INSPECTION,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId())
                .content(objectMapper.writeValueAsBytes(inspectionValueDto));

        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }

    @Test
    public void putDrtInspectionForbidden() throws Exception {
        dataSetInitializer.createUserFeature(FeatureCode.DRT_INTERNAL_INSPECTION, dataset.user_simpleUser, EnumRightLevel.NONE);
        asUser = dataset.user_simpleUser;

        withRequest = put(API_DRT_INSPECTION,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId())
                .content(objectMapper.writeValueAsBytes(inspectionValueDto));

        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }

    //TODO_LIST
    @Test
    public void putDrtInspectionTodoListWithoutControl() throws Exception {
        withRequest = put(API_DRT_INSPECTION,
                drtDatasetWithoutControl.drt.getId(),
                drtDatasetWithoutControl.requestDataset.routingDataset.operationPreliminary.getNaturalId(),
                drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListPreliminary.getNaturalId())
                .content(objectMapper.writeValueAsBytes(inspectionValueDto));
        expectedStatus = HttpStatus.NO_CONTENT;
        abstractCheck();
    }

    @Test
    public void putDrtInspectionTodoListWithControl() throws Exception {
        withRequest = put(API_DRT_INSPECTION,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationPreliminary.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListPreliminary.getNaturalId())
                .content(objectMapper.writeValueAsBytes(inspectionValueDto));
        expectedStatus = HttpStatus.NO_CONTENT;
        abstractCheck();
    }

    //RC
    @Test
    public void putDrtInspectionDimensionalWithoutControl() throws Exception {
        InspectionPostValueDto inspectionPostValueDto = new InspectionPostValueDto();
        inspectionPostValueDto.setPostThresholdId(drtDatasetWithoutControl.requestDataset.routingDataset.routingFunctionalAreaPostDimensional.getNaturalId());
        inspectionPostValueDto.setValue(54F);

        InspectionStepActivationDto inspectionStepActivationDto = new InspectionStepActivationDto();
        inspectionStepActivationDto.setPosts(List.of(inspectionPostValueDto));
        inspectionStepActivationDto.setId(drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional.getNaturalId());
        inspectionValueDto.setSteps(List.of(inspectionStepActivationDto));

        withRequest = put(API_DRT_INSPECTION,
                drtDatasetWithoutControl.drt.getId(),
                drtDatasetWithoutControl.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId())
                .content(objectMapper.writeValueAsBytes(inspectionValueDto));
        expectedStatus = HttpStatus.NO_CONTENT;
        abstractCheck();
    }

    @Test
    public void putDrtInspectionDimensionalWithControl() throws Exception {
        InspectionPostValueDto inspectionPostValueDto = new InspectionPostValueDto();
        inspectionPostValueDto.setPostThresholdId(drtDataset.requestDataset.routingDataset.routingFunctionalAreaPostDimensional.getNaturalId());
        inspectionPostValueDto.setValue(54F);
        InspectionStepActivationDto inspectionStepActivationDto = new InspectionStepActivationDto();
        inspectionStepActivationDto.setPosts(List.of(inspectionPostValueDto));
        inspectionStepActivationDto.setId(drtDataset.requestDataset.routingDataset.stepActivationDimensional.getNaturalId());
        inspectionValueDto.setSteps(List.of(inspectionStepActivationDto));

        withRequest = put(API_DRT_INSPECTION,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId())
                .content(objectMapper.writeValueAsBytes(inspectionValueDto));
        expectedStatus = HttpStatus.NO_CONTENT;
        abstractCheck();
    }


    //VISUAL
    @Test
    public void putDrtInspectionVisualWithoutControl() throws Exception {
        InspectionPostValueDto inspectionPostValueDto = new InspectionPostValueDto();
        inspectionPostValueDto.setPostThresholdId(drtDatasetWithoutControl.requestDataset.routingDataset.routingFunctionalAreaPostVisual.getNaturalId());
        inspectionPostValueDto.setValue(Boolean.FALSE);
        InspectionStepActivationDto inspectionStepActivationDto = new InspectionStepActivationDto();
        inspectionStepActivationDto.setPosts(List.of(inspectionPostValueDto));
        inspectionStepActivationDto.setId(drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationVisual.getNaturalId());
        inspectionValueDto.setSteps(List.of(inspectionStepActivationDto));

        withRequest = put(API_DRT_INSPECTION,
                drtDatasetWithoutControl.drt.getId(),
                drtDatasetWithoutControl.requestDataset.routingDataset.operationVisual.getNaturalId(),
                drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId())
                .content(objectMapper.writeValueAsBytes(inspectionValueDto));
        expectedStatus = HttpStatus.NO_CONTENT;
        abstractCheck();
    }

    @Test
    public void putDrtInspectionVisualWithControl() throws Exception {
        InspectionPostValueDto inspectionPostValueDto = new InspectionPostValueDto();
        inspectionPostValueDto.setPostThresholdId(drtDataset.requestDataset.routingDataset.routingFunctionalAreaPostVisual.getNaturalId());
        inspectionPostValueDto.setValue(Boolean.FALSE);
        InspectionStepActivationDto inspectionStepActivationDto = new InspectionStepActivationDto();
        inspectionStepActivationDto.setPosts(List.of(inspectionPostValueDto));
        inspectionStepActivationDto.setId(drtDataset.requestDataset.routingDataset.stepActivationVisual.getNaturalId());
        inspectionValueDto.setSteps(List.of(inspectionStepActivationDto));

        withRequest = put(API_DRT_INSPECTION,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationVisual.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId())
                .content(objectMapper.writeValueAsBytes(inspectionValueDto));
        expectedStatus = HttpStatus.NO_CONTENT;
        abstractCheck();
    }

}
