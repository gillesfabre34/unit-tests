package com.airbus.retex.drt;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.inspection.InspectionPostValueDto;
import com.airbus.retex.business.dto.inspection.InspectionQCheckValueDto;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class DrtPutInspectionQCheckControllerIT extends BaseControllerTest {

    private static final String API_DRT = "/api/drts/";
    private static final String API_DRT_INSPECTION_QCHECK = API_DRT + "{id}/operations/{operationId}/tasks/{taskId}/qcheck";

    @Autowired
    private DatasetFactory datasetFactory;

    private DrtDataset drtDataset;
    private List<InspectionQCheckValueDto> inspectionQCheckValueDtoList;

    @BeforeEach
    private void before() {
        runInTransaction(() -> {
            //DATASET WITH CONTROL
            drtDataset = datasetFactory.createDrtDataset();
            drtDataset.drtOperationStatusDimensional.setStatus(EnumStatus.Q_CHECK);
        });
        asUser = dataSetInitializer.createUserWithRole("QUALITY_CONTROLLER", "QUALITY_CONTROLLER", RoleCode.QUALITY_CONTROLLER);
        dataSetInitializer.createUserFeature(FeatureCode.DRT_QUALITY_CHECK, asUser, EnumRightLevel.WRITE);

        InspectionQCheckValueDto inspectionQCheckValueDto = new InspectionQCheckValueDto();
        inspectionQCheckValueDto.setIdRoutingComponentIndex(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexDimensional.getNaturalId());
        inspectionQCheckValueDto.setQCheckValue(true);
        inspectionQCheckValueDtoList = List.of(inspectionQCheckValueDto);
    }


    @Test
    public void putDrtInspectionWriteLevelOk() throws Exception {
        withRequest = put(API_DRT_INSPECTION_QCHECK,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId())
                .content(objectMapper.writeValueAsBytes(inspectionQCheckValueDtoList));

        expectedStatus = HttpStatus.NO_CONTENT;
        abstractCheck();
    }

    @Test
    public void putDrtInspectionReadLevelKo() throws Exception {
        dataSetInitializer.createUserFeature(FeatureCode.DRT_QUALITY_CHECK, dataset.user_simpleUser, EnumRightLevel.READ);
        asUser = dataset.user_simpleUser;

        withRequest = put(API_DRT_INSPECTION_QCHECK,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId())
                .content(objectMapper.writeValueAsBytes(inspectionQCheckValueDtoList));

        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }

    @Test
    public void putDrtInspectionOperatorKo() throws Exception {
        asUser = dataSetInitializer.createUserWithRole("operateur", "operateur", RoleCode.INTERNAL_OPERATOR);
        dataSetInitializer.createUserFeature(FeatureCode.DRT_INTERNAL_INSPECTION, dataset.user_simpleUser, EnumRightLevel.WRITE);
        asUser = dataset.user_simpleUser;

        withRequest = put(API_DRT_INSPECTION_QCHECK,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId())
                .content(objectMapper.writeValueAsBytes(inspectionQCheckValueDtoList));

        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }

    @Test
    public void putDrtInspectionOperatorWithFeatureForbidden() throws Exception {
        asUser = dataSetInitializer.createUserWithRole("operateur", "operateur", RoleCode.INTERNAL_OPERATOR);
        dataSetInitializer.createUserFeature(FeatureCode.DRT_QUALITY_CHECK, dataset.user_simpleUser, EnumRightLevel.WRITE);
        asUser = dataset.user_simpleUser;

        withRequest = put(API_DRT_INSPECTION_QCHECK,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId())
                .content(objectMapper.writeValueAsBytes(inspectionQCheckValueDtoList));

        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }
    @Test
    public void putDrtInspectionForbidden() throws Exception {
        dataSetInitializer.createUserFeature(FeatureCode.DRT_QUALITY_CHECK, dataset.user_simpleUser, EnumRightLevel.NONE);
        asUser = dataset.user_simpleUser;

        withRequest = put(API_DRT_INSPECTION_QCHECK,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId())
                .content(objectMapper.writeValueAsBytes(inspectionQCheckValueDtoList));

        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }


}
