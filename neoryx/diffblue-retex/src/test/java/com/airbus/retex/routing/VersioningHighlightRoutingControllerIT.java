package com.airbus.retex.routing;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.functionalArea.FunctionalAreaCreateOrUpdateDto;
import com.airbus.retex.business.dto.part.PartCreateUpdateFunctionalAreaDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.classification.EnumClassification;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.StepType;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.post.RoutingFunctionalAreaPost;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.step.StepActivation;
import com.airbus.retex.service.part.IPartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
public class VersioningHighlightRoutingControllerIT extends BaseControllerTest {

    @Autowired
    private IPartService partService;

    private Routing routing;

    @BeforeEach
    public void before() {
        routing = initializeRouting();
    }

    @Test
    public void updatePartValidatedVersion_UpdatedOk() throws Exception {
        try {
            updatePartWithNewFunctionalArea(Boolean.TRUE);
        } catch (FunctionalException e) {
            AssertionErrors.fail("Fail to update part, this should not happen :(");
        }

        callGetRoutingOperations()
                .andExpect(jsonPath("operations", hasSize(1)))
                .andExpect(jsonPath("functionalAreas", hasSize(1)))
                .andExpect(jsonPath("functionalAreas[0].functionalAreaName.id", equalTo(dataset.functionalAreaName_OuterRingBottom.getId().intValue())));

        callPartHighlight(routing.getNaturalId(), HttpStatus.NO_CONTENT);

        callGetRoutingOperations()
                .andExpect(jsonPath("operations", hasSize(1)))
                .andExpect(jsonPath("functionalAreas", hasSize(2)))
                .andExpect(jsonPath("functionalAreas[0].functionalAreaName.id", equalTo(dataset.functionalAreaName_OuterRingBottom.getId().intValue())))
                .andExpect(jsonPath("functionalAreas[1].functionalAreaName.id", equalTo(dataset.functionalAreaName_RingBottom.getId().intValue())));
    }

    @Test
    public void updatePartCreatedVersion_NoChanges() throws Exception {
        try {
            updatePartWithNewFunctionalArea(Boolean.FALSE);
        } catch (FunctionalException e) {
            AssertionErrors.fail("Fail to update part, this should not happen :(");
        }

        callGetRoutingOperations()
                .andExpect(jsonPath("operations", hasSize(1)))
                .andExpect(jsonPath("functionalAreas", hasSize(1)))
                .andExpect(jsonPath("functionalAreas[0].functionalAreaName.id", equalTo(dataset.functionalAreaName_OuterRingBottom.getId().intValue())));

        callPartHighlight(routing.getNaturalId(), HttpStatus.NO_CONTENT);

        callGetRoutingOperations()
                .andExpect(jsonPath("operations", hasSize(1)))
                .andExpect(jsonPath("functionalAreas", hasSize(1)))
                .andExpect(jsonPath("functionalAreas[0].functionalAreaName.id", equalTo(dataset.functionalAreaName_OuterRingBottom.getId().intValue())));
    }

    @Test
    public void updatePartVersion_InvalidRouting() throws Exception {
        callPartHighlight(666L, HttpStatus.NOT_FOUND)
                .andExpect(jsonPath("messages").value("Routing not found"));
    }

    @Test
    public void updatePartVersion_InvalidPart() throws Exception {
        runInTransaction(() -> {
            routing.setPart(dataSetInitializer.createPart(p -> {
                p.setStatus(EnumStatus.CREATED);
            }, null));
        });

        callPartHighlight(routing.getNaturalId(), HttpStatus.NOT_FOUND)
                .andExpect(jsonPath("messages").value("The part not exists."));
    }

    private ResultActions callPartHighlight(Long routingId, HttpStatus status) throws Exception {
        expectedStatus = status;
        asUser = dataset.user_superAdmin;
        withRequest = put("/api/routings/" + routingId + "/update-part/");

        return abstractCheck();
    }

    private ResultActions callGetRoutingOperations() throws Exception {
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_superAdmin;
        withRequest = get("/api/routings/" + routing.getNaturalId() + "/operations");

        return abstractCheck();
    }

    private void updatePartWithNewFunctionalArea(Boolean validatePart) throws FunctionalException {
        FunctionalArea functionalArea = routing.getPart().getFunctionalAreas().iterator().next();

        // current FunctionalArea
        FunctionalAreaCreateOrUpdateDto faDto1 = new FunctionalAreaCreateOrUpdateDto();
        faDto1.setId(functionalArea.getNaturalId());
        faDto1.setAreaNumber(functionalArea.getAreaNumber());
        faDto1.setFaNameId(functionalArea.getFunctionalAreaName().getId());
        faDto1.setFunctionalityId(functionalArea.getFunctionality().getId());
        faDto1.setClassification(functionalArea.getClassification());
        faDto1.setDisabled(functionalArea.isDisabled());
        faDto1.setMaterial(functionalArea.getMaterial());
        faDto1.setTreatmentId(functionalArea.getTreatment().getId());
        // new FunctionalArea
        FunctionalAreaCreateOrUpdateDto faDto2 = new FunctionalAreaCreateOrUpdateDto();
        faDto2.setAreaNumber(functionalArea.getAreaNumber() + 1);
        faDto2.setFunctionalityId(dataset.functionality_splines.getId());
        faDto2.setDisabled(false);
        faDto2.setFaNameId(dataset.functionalAreaName_RingBottom.getId());
        faDto2.setClassification(EnumClassification.ZHS);

        List<FunctionalAreaCreateOrUpdateDto> faDtos = new ArrayList<>();
        faDtos.add(faDto1);
        faDtos.add(faDto2);

        PartCreateUpdateFunctionalAreaDto dto = new PartCreateUpdateFunctionalAreaDto();
        dto.setFunctionalAreas(faDtos);

        partService.createOrUpdateFunctionalityAreas(routing.getPart().getNaturalId(), validatePart, dto);
    }

    private Routing initializeRouting() {
        // routing component
        RoutingComponent rc = dataSetInitializer.createRoutingComponent(component -> {
            component.setFunctionality(dataset.functionality_teeth);
        });
        dataSetInitializer.createRoutingComponentIndex(rc.getTechnicalId(), null);
        Step step = dataSetInitializer.createStep(s -> {
            s.setRoutingComponent(rc);
            s.setStepNumber(1);
            s.setType(StepType.AUTO);
            s.setFiles(new HashSet<>());
        });
        Post post = dataSetInitializer.createPost("blabla FR", "blabla EN");
        step.addPost(post);

        // part
        Part part = dataSetInitializer.createPart(p -> {
            p.setPartNumber("666");
            p.setPartNumberRoot(null);
            p.setStatus(EnumStatus.VALIDATED);
        }, null);

        FunctionalArea functionalArea = dataSetInitializer.createFunctionalArea(fa -> {
            fa.setFunctionality(dataset.functionality_teeth);
        });
        part.addFunctionalAreas(functionalArea);

        // routing
        Routing routing = dataSetInitializer.createRouting(part);
        Operation operation = dataSetInitializer.createOperation(1, ope -> {
            ope.setOperationType(dataset.operationType_dimensional);
            ope.setRouting(routing);
        });
        routing.addOperation(operation);
        OperationFunctionalArea operationFunctionalArea = dataSetInitializer.createOperationFunctionalArea(ofa -> {
            ofa.setOperation(operation);
            ofa.setFunctionalArea(functionalArea);
        });

        StepActivation stepActivation = dataSetInitializer.createStepActivation(true, step, operationFunctionalArea);
        operationFunctionalArea.addStepActivation(stepActivation);

        RoutingFunctionalAreaPost routingFunctionalAreaPost = dataSetInitializer.createRoutingFunctionalAreaPost(rfap -> {
            rfap.setStepActivation(stepActivation);
            rfap.setPost(post);
            rfap.setThreshold(5.0f);
        });
        stepActivation.addRoutingFunctionalAreaPost(routingFunctionalAreaPost);

        return routing;
    }
}
