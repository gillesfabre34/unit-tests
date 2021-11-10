package com.airbus.retex.drt;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.dataset.DatasetFactory;
import com.airbus.retex.dataset.DrtDataset;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.DrtPictures;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.post.RoutingFunctionalAreaPost;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.step.StepActivation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class DrtInspectionControllerIT extends BaseControllerTest {
    private static final String API_DRT = "/api/drts/";
    private static final String API_DRT_INSPECTION = API_DRT + "{id}/operations/{operationId}/tasks/{taskId}";
    private static final String EMPTY_LIST = API_DRT + "[]";

    @Autowired
    private DatasetFactory datasetFactory;

    private DrtDataset drtDatasetWithoutControl;
    private DrtDataset drtDataset;
    private ResultActions results;

    @BeforeEach
    private void before() {
        runInTransaction(() -> {
            //DATASET WITHOUT CONTROL
            drtDatasetWithoutControl = new DrtDataset();
            drtDatasetWithoutControl.requestDataset = datasetFactory.createRequestDataset();
            drtDatasetWithoutControl.drt = dataSetInitializer.createDRT(drt -> {
                drt.setChildRequest(drtDatasetWithoutControl.requestDataset.childRequest);
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
        dataSetInitializer.createUserFeature(FeatureCode.DRT_INTERNAL_INSPECTION, dataset.user_simpleUser, EnumRightLevel.READ);
        asUser = dataset.user_simpleUser;
    }


    @Test
    public void getDrtInspectionWriteLevelOk() throws Exception {
        dataSetInitializer.createUserFeature(FeatureCode.DRT_INTERNAL_INSPECTION, dataset.user_simpleUser, EnumRightLevel.WRITE);
        asUser = dataset.user_simpleUser;

        withRequest = get(API_DRT_INSPECTION,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId());
        expectedStatus = HttpStatus.OK;
        abstractCheck();
    }

    @Test
    public void getDrtInspectionForbidden() throws Exception {
        dataSetInitializer.createUserFeature(FeatureCode.DRT_INTERNAL_INSPECTION, dataset.user_simpleUser, EnumRightLevel.NONE);
        asUser = dataset.user_simpleUser;

        withRequest = get(API_DRT_INSPECTION,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId());
        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }

    //ROUTING_COMPONENT
    @Test
    public void getDrtInspectionDimensionalWithoutControl() throws Exception {
        withRequest = get(API_DRT_INSPECTION,
                drtDatasetWithoutControl.drt.getId(),
                drtDatasetWithoutControl.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId());
        expectedStatus = HttpStatus.OK;
        results = abstractCheck();
        results.andExpect(jsonPath("$[0].idRoutingComponentIndex")
                .value(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexDimensional.getNaturalId()))
                .andExpect(jsonPath("$[0].version")
                        .value(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexDimensional.getVersionNumber()))
                .andExpect(jsonPath("$[0].todoListValue", nullValue()))
                .andExpect(jsonPath("$[0].behavior").value(drtDatasetWithoutControl.requestDataset.routingDataset.operationDimensional.getOperationType().getBehavior().toString()))
                .andExpect(jsonPath("$[0].damage.name", notNullValue()))
                .andExpect(jsonPath("$[0].damage.id").value(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.damageDataset.damage.getNaturalId()));
        checkStepResults(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.stepRoutingComponentDimensional);
        checkStepActivationResults(drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationDimensional, null);
        checkPostResults(drtDatasetWithoutControl.requestDataset.routingDataset.routingFunctionalAreaPostDimensional,
                drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.postRoutingComponentDimensional, null);
    }


    @Test
    public void getDrtInspectionDimensionalWithControl() throws Exception {
        withRequest = get(API_DRT_INSPECTION,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId());
        expectedStatus = HttpStatus.OK;
        results = abstractCheck();
        results.andExpect(jsonPath("$[0].idRoutingComponentIndex")
                .value(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexDimensional.getNaturalId()))
                .andExpect(jsonPath("$[0].version")
                        .value(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexDimensional.getVersionNumber()))
                .andExpect(jsonPath("$[0].todoListValue", nullValue()))
                .andExpect(jsonPath("$[0].behavior").value(drtDataset.requestDataset.routingDataset.operationDimensional.getOperationType().getBehavior().toString()))
                .andExpect(jsonPath("$[0].damage.name", notNullValue()))
                .andExpect(jsonPath("$[0].damage.id").value(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.damageDataset.damage.getNaturalId()));
        checkStepResults(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.stepRoutingComponentDimensional);
        checkStepActivationResults(drtDataset.requestDataset.routingDataset.stepActivationDimensional, drtDataset.drtPicturesDimensional);
        checkPostResults(drtDataset.requestDataset.routingDataset.routingFunctionalAreaPostDimensional,
                drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.postRoutingComponentDimensional,
                drtDataset.controlDimensional.getValue());
    }

    @Test
    public void getDrtInspectionLaboratoryWithQcheckTrue() throws Exception {
        withRequest = get(API_DRT_INSPECTION,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationLaboratory.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId());
        expectedStatus = HttpStatus.OK;
        results = abstractCheck();
        results.andExpect(jsonPath("$[0].idRoutingComponentIndex")
                .value(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexLaboratory.getNaturalId()))
                .andExpect(jsonPath("$[0].todoListValue", nullValue()))
                .andExpect(jsonPath("$[0].behavior").value(drtDataset.requestDataset.routingDataset.operationLaboratory.getOperationType().getBehavior().toString()))
                .andExpect(jsonPath("$[0].damage.name", notNullValue()))
                .andExpect(jsonPath("$[0].qcheckValue").value(true))
                .andExpect(jsonPath("$[0].damage.id").value(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.damageDataset.damage.getNaturalId()));
        checkStepResults(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.stepRoutingComponentLaboratory);
        checkStepActivationResults(drtDataset.requestDataset.routingDataset.stepActivationLaboratory, drtDataset.drtPicturesLaboratory);
        checkPostResults(drtDataset.requestDataset.routingDataset.routingFunctionalAreaPostLaboratory,
                drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.postRoutingComponentLaboratory,
                drtDataset.controlLaboratory.getValue());
    }

    @Test
    public void getDrtInspectionLaboratoryWithQcheckFalse() throws Exception {
        withRequest = get(API_DRT_INSPECTION,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationTridimensional.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId());
        expectedStatus = HttpStatus.OK;
        results = abstractCheck();
        results.andExpect(jsonPath("$[0].idRoutingComponentIndex")
                .value(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexTridimensional.getNaturalId()))
                .andExpect(jsonPath("$[0].todoListValue", nullValue()))
                .andExpect(jsonPath("$[0].behavior").value(drtDataset.requestDataset.routingDataset.operationTridimensional.getOperationType().getBehavior().toString()))
                .andExpect(jsonPath("$[0].damage.name", notNullValue()))
                .andExpect(jsonPath("$[0].qcheckValue").value(false))
                .andExpect(jsonPath("$[0].damage.id").value(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.damageDataset.damage.getNaturalId()));
        checkStepResults(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.stepRoutingComponentTridimensional);
        checkStepActivationResults(drtDataset.requestDataset.routingDataset.stepActivationTridimensional, drtDataset.drtPicturesTriDimensional);
        checkPostResults(drtDataset.requestDataset.routingDataset.routingFunctionalAreaPostTridimensional,
                drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.postRoutingComponentTridimensional,
                drtDataset.controlTriDimensional.getValue());
    }

    //VISUAL
    @Test
    public void getDrtInspectionVisualWithoutControl() throws Exception {
        withRequest = get(API_DRT_INSPECTION,
                drtDatasetWithoutControl.drt.getId(),
                drtDatasetWithoutControl.requestDataset.routingDataset.operationVisual.getNaturalId(),
                drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId());
        expectedStatus = HttpStatus.OK;
        results = abstractCheck();
        results.andExpect(jsonPath("$[0].idRoutingComponentIndex")
                .value(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexVisual.getNaturalId()))
                .andExpect(jsonPath("$[0].version")
                        .value(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexVisual.getVersionNumber()))
                .andExpect(jsonPath("$[0].todoListValue", nullValue()))
                .andExpect(jsonPath("$[0].behavior").value(drtDatasetWithoutControl.requestDataset.routingDataset.operationVisual.getOperationType().getBehavior().toString()))
                .andExpect(jsonPath("$[0].damage.name", notNullValue()))
                .andExpect(jsonPath("$[0].damage.id").value(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.damageDataset.damage.getNaturalId()));
        checkStepResults(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.stepRoutingComponentVisual);
        checkStepActivationResults(drtDatasetWithoutControl.requestDataset.routingDataset.stepActivationVisual, null);
        checkPostResults(drtDatasetWithoutControl.requestDataset.routingDataset.routingFunctionalAreaPostVisual,
                drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.postRoutingComponentVisual,
                null);
    }

    @Test
    public void getDrtInspectionVisualWithControl() throws Exception {
        withRequest = get(API_DRT_INSPECTION,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationVisual.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId());
        expectedStatus = HttpStatus.OK;
        results = abstractCheck();
        results.andExpect(jsonPath("$[0].idRoutingComponentIndex")
                .value(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexVisual.getNaturalId()))
                .andExpect(jsonPath("$[0].version")
                        .value(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexVisual.getVersionNumber()))
                .andExpect(jsonPath("$[0].todoListValue", nullValue()))
                .andExpect(jsonPath("$[0].behavior").value(drtDataset.requestDataset.routingDataset.operationVisual.getOperationType().getBehavior().toString()))
                .andExpect(jsonPath("$[0].damage.name", notNullValue()))
                .andExpect(jsonPath("$[0].damage.id").value(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.damageDataset.damage.getNaturalId()));
        checkStepResults(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.stepRoutingComponentVisual);
        checkStepActivationResults(drtDataset.requestDataset.routingDataset.stepActivationVisual, null);
        checkPostResults(drtDataset.requestDataset.routingDataset.routingFunctionalAreaPostVisual,
                drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.postRoutingComponentVisual,
                drtDataset.controlVisual.getValue());
    }


    //TODO_LIST
    @Test
    public void getDrtInspectionTodoListWithoutControl() throws Exception {
        withRequest = get(API_DRT_INSPECTION,
                drtDatasetWithoutControl.drt.getId(),
                drtDatasetWithoutControl.requestDataset.routingDataset.operationPreliminary.getNaturalId(),
                drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListPreliminary.getNaturalId());
        expectedStatus = HttpStatus.OK;
        results = abstractCheck();
        results.andExpect(jsonPath("$[0].todoListValue", nullValue()))
                .andExpect(jsonPath("$[0].idRoutingComponentIndex")
                        .value(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexPreliminary.getNaturalId()))
                .andExpect(jsonPath("$[0].version")
                        .value(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexPreliminary.getVersionNumber()))
                .andExpect(jsonPath("$[0].todoListName.id")
                        .value(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexPreliminary.getTodoList().getTodoListNameId()))
                .andExpect(jsonPath("$[0].behavior").value(drtDatasetWithoutControl.requestDataset.routingDataset.operationPreliminary.getOperationType().getBehavior().toString()))
                .andExpect(jsonPath("$[0].damage", nullValue()))
                .andExpect(jsonPath("$[0].steps[0].stepActivation", nullValue()));
        checkStepResults(drtDatasetWithoutControl.requestDataset.routingDataset.partDataset.routingComponentDataset.stepTodoListPreliminary);
    }

    @Test
    public void getDrtInspectionTodoListWithControl() throws Exception {
        withRequest = get(API_DRT_INSPECTION,
                drtDataset.drt.getId(),
                drtDataset.requestDataset.routingDataset.operationPreliminary.getNaturalId(),
                drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListPreliminary.getNaturalId());
        expectedStatus = HttpStatus.OK;
        results = abstractCheck();
        results.andExpect(jsonPath("$[0].todoListValue").value(drtDataset.controlPremiminary.getValue().toString()))
                .andExpect(jsonPath("$[0].idRoutingComponentIndex")
                        .value(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexPreliminary.getNaturalId()))
                .andExpect(jsonPath("$[0].version")
                        .value(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexPreliminary .getVersionNumber()))
                .andExpect(jsonPath("$[0].todoListName.id")
                        .value(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexPreliminary.getTodoList().getTodoListNameId()))
                .andExpect(jsonPath("$[0].behavior").value(drtDataset.requestDataset.routingDataset.operationPreliminary.getOperationType().getBehavior().toString()))
                .andExpect(jsonPath("$[0].damage", nullValue()))
                .andExpect(jsonPath("$[0].steps[0].stepActivation", nullValue()));
        checkStepResults(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.stepTodoListPreliminary);
    }

    private void checkStepResults(Step step) throws Exception {
        results.andExpect(jsonPath("$[0].steps", notNullValue()))
                .andExpect(jsonPath("$[0].steps[0]", notNullValue()))
                .andExpect(jsonPath("$[0].steps[0].id").value(step.getNaturalId()))
                .andExpect(jsonPath("$[0].steps[0].information", notNullValue()))
                .andExpect(jsonPath("$[0].steps[0].name", notNullValue()))
                .andExpect(jsonPath("$[0].steps[0].stepNumber").value(step.getStepNumber()))
                .andExpect(jsonPath("$[0].steps[0].files[0].uuid").value(step.getFiles().iterator().next().getUuid().toString()))
                .andExpect(jsonPath("$[0].steps[0].files[0].filename").value(step.getFiles().iterator().next().getFilename()))
                .andExpect(jsonPath("$[0].steps[0].type").value(step.getType().toString()))
                .andExpect(jsonPath("$[0].steps[0].posts", notNullValue()));
    }

    private void checkStepActivationResults(StepActivation stepActivation, @Nullable DrtPictures drtPictures) throws Exception {
        results.andExpect(jsonPath("$[0].steps[0].stepActivation.id").value(stepActivation.getNaturalId()))
                .andExpect(jsonPath("$[0].steps[0].stepActivation.activated").value(stepActivation.getActivated()))
                .andExpect(jsonPath("$[0].steps[0].information", notNullValue()));
        if (null == drtPictures) {
            results.andExpect(jsonPath("$[0].steps[0].stepActivation.drtPictures", nullValue()));
        } else {
            results.andExpect(jsonPath("$[0].steps[0].stepActivation.drtPictures", notNullValue()))
                    .andExpect(jsonPath("$[0].steps[0].stepActivation.drtPictures.id").value(drtPictures.getId()))
                    .andExpect(jsonPath("$[0].steps[0].stepActivation.drtPictures.images[0].uuid").value(drtPictures.getMedias().iterator().next().getUuid().toString()))
                    .andExpect(jsonPath("$[0].steps[0].stepActivation.drtPictures.images[0].filename").value(drtPictures.getMedias().iterator().next().getFilename()));

        }
    }

    private void checkPostResults(RoutingFunctionalAreaPost routingFunctionalAreaPost, Post post, Object value) throws Exception {
        results.andExpect(jsonPath("$[0].steps[0].posts[0]", notNullValue()))
                .andExpect(jsonPath("$[0].steps[0].posts[0].designation", notNullValue()))
                .andExpect(jsonPath("$[0].steps[0].posts[0].postThreshold.id").value(routingFunctionalAreaPost.getNaturalId()))
                .andExpect(jsonPath("$[0].steps[0].posts[0].postThreshold.threshold").value(routingFunctionalAreaPost.getThreshold()))
                .andExpect(jsonPath("$[0].steps[0].posts[0].measureUnit", notNullValue()))
                .andExpect(jsonPath("$[0].steps[0].posts[0].measureUnit.id").value(post.measureUnitId))
                .andExpect(jsonPath("$[0].steps[0].posts[0].measureUnit.name", notNullValue()))
                .andExpect(jsonPath("$[0].steps[0].posts[0].id").value(post.getNaturalId()))
                .andExpect(jsonPath("$[0].steps[0].posts[0].value").value((value != null) ? value : null));
    }
}