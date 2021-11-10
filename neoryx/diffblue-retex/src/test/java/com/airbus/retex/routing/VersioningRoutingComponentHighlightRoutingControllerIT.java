package com.airbus.retex.routing;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.post.PostCreationDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentCreateUpdateDto;
import com.airbus.retex.business.dto.step.StepCreationDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.common.StepType;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.post.PostFieldsEnum;
import com.airbus.retex.model.post.RoutingFunctionalAreaPost;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.step.StepActivation;
import com.airbus.retex.model.step.StepFieldsEnum;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.service.routingComponent.IRoutingComponentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class VersioningRoutingComponentHighlightRoutingControllerIT extends BaseControllerTest {

    @Autowired
    private IRoutingComponentService routingComponentService;

    private Routing routing;

    private RoutingComponentIndex rciRoutingComponent;

    private RoutingComponentIndex rciTodoList;

    private Operation operationTL;

    private Operation operation;

    @BeforeEach
    public void before() {
        runInTransaction(() -> {
            routing = initializeRouting();
        });
    }

    @Test
    public void updateRoutingComponentValidatedVersion_UpdatedOk() throws Exception {
        updateRoutingComponent(Boolean.TRUE);

        FunctionalArea fa = operation.getOperationFunctionalAreas().iterator().next().getFunctionalArea();

        callGetRoutingOperationsTasks(operation.getNaturalId(), fa.getNaturalId())
                .andExpect(jsonPath("$.inspectionDetail[0].steps", hasSize(1)))
                .andExpect(jsonPath("$.inspectionDetail[0].status", equalTo(EnumStatus.OBSOLETED.name())));

        callComponentHighlight(routing.getNaturalId(), rciRoutingComponent.getNaturalId(), HttpStatus.NO_CONTENT);

        callGetRoutingOperationsTasks(operation.getNaturalId(), fa.getNaturalId())
                .andExpect(jsonPath("$.inspectionDetail[0].steps", hasSize(2)))
                .andExpect(jsonPath("$.inspectionDetail[0].status", equalTo(EnumStatus.VALIDATED.name())));
    }

    @Test
    public void updateRoutingComponentCreatedVersion_NoChanges() throws Exception {
        updateRoutingComponent(Boolean.FALSE);

        FunctionalArea fa = operation.getOperationFunctionalAreas().iterator().next().getFunctionalArea();

        callGetRoutingOperationsTasks(operation.getNaturalId(), fa.getNaturalId())
                .andExpect(jsonPath("$.inspectionDetail", hasSize(1)))
                .andExpect(jsonPath("$.inspectionDetail[0].status", equalTo(EnumStatus.VALIDATED.name())));

        callComponentHighlight(routing.getNaturalId(), rciRoutingComponent.getNaturalId(), HttpStatus.NO_CONTENT);

        callGetRoutingOperationsTasks(operation.getNaturalId(), fa.getNaturalId())
                .andExpect(jsonPath("$.inspectionDetail", hasSize(1)))
                .andExpect(jsonPath("$.inspectionDetail[0].status", equalTo(EnumStatus.VALIDATED.name())));
    }

    @Test
    public void updateTodoListValidatedVersion_UpdatedOk() throws Exception {
        updateTodoList(Boolean.TRUE);

        TodoList todoList = operationTL.getTodoLists().iterator().next();

        callGetRoutingOperationsTasks(operationTL.getNaturalId(), todoList.getNaturalId())
                .andExpect(jsonPath("$.inspectionDetail[0].steps", hasSize(1)))
                .andExpect(jsonPath("$.inspectionDetail[0].status", equalTo(EnumStatus.OBSOLETED.name())))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[*].information", hasItem("information Step")));

        callComponentHighlight(routing.getNaturalId(), rciTodoList.getNaturalId(), HttpStatus.NO_CONTENT);

        callGetRoutingOperationsTasks(operationTL.getNaturalId(), todoList.getNaturalId())
                .andExpect(jsonPath("$.inspectionDetail[0].steps", hasSize(1)))
                .andExpect(jsonPath("$.inspectionDetail[0].status", equalTo(EnumStatus.VALIDATED.name())))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[*].information", hasItem("step updated EN")));
    }

    @Test
    public void updateTodoListCreatedVersion_NoChanges() throws Exception {
        updateTodoList(Boolean.FALSE);

        TodoList todoList = operationTL.getTodoLists().iterator().next();

        callGetRoutingOperationsTasks(operationTL.getNaturalId(), todoList.getNaturalId())
                .andExpect(jsonPath("$.inspectionDetail[0].steps", hasSize(1)))
                .andExpect(jsonPath("$.inspectionDetail[0].status", equalTo(EnumStatus.VALIDATED.name())))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[*].information", hasItem("information Step")));

        callComponentHighlight(routing.getNaturalId(), rciTodoList.getNaturalId(), HttpStatus.NO_CONTENT);

        callGetRoutingOperationsTasks(operationTL.getNaturalId(), todoList.getNaturalId())
                .andExpect(jsonPath("$.inspectionDetail[0].steps", hasSize(1)))
                .andExpect(jsonPath("$.inspectionDetail[0].status", equalTo(EnumStatus.VALIDATED.name())))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[*].information", hasItem("information Step")));
    }

    @Test
    public void updateComponentVersion_InvalidRouting() throws Exception {
        callComponentHighlight(666L, rciRoutingComponent.getNaturalId(), HttpStatus.NOT_FOUND)
                .andExpect(jsonPath("messages").value("Routing not found"));
    }

    @Test
    public void updateComponentVersion_InvalidComponent() throws Exception {
        runInTransaction(() -> {
            routing.setPart(dataSetInitializer.createPart(p -> {
                p.setStatus(EnumStatus.CREATED);
            }, null));
        });

        callComponentHighlight(routing.getNaturalId(), 999L, HttpStatus.NOT_FOUND)
                .andExpect(jsonPath("messages").value("Routing Component index not found"));
    }

    private ResultActions callComponentHighlight(Long routingId, Long componentId, HttpStatus status) throws Exception {
        // then call highlight
        expectedStatus = status;
        asUser = dataset.user_superAdmin;
        withRequest = put("/api/routings/" + routingId + "/update-routing-component/" + componentId);

        return abstractCheck();
    }

    private ResultActions callGetRoutingOperationsTasks(Long operationId, Long taskId) throws Exception {
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_superAdmin;
        withRequest = get("/api/routings/" + routing.getNaturalId() + "/operations/" + operationId + "/tasks/" + taskId);

        return abstractCheck();
    }

    private void updateRoutingComponent(Boolean validateComponent) {
        runInTransaction(() -> {
            RoutingComponent rc = rciRoutingComponent.getRoutingComponent();

            // existing step
            Step step = rc.getSteps().get(0);
            StepCreationDto stepDto = new StepCreationDto();
            stepDto.setId(step.getNaturalId());
            stepDto.setType(step.getType());
            stepDto.setIsDeletable(false);
            stepDto.setTranslatedFields(dataSetInitializer.toTranslationFields(step.getTranslations()));

            Post post = step.getPosts().iterator().next();
            PostCreationDto postDto = new PostCreationDto();
            postDto.setMeasureUnitId(post.getMeasureUnit().getId());
            postDto.setIsDeletable(false);
            postDto.setTranslatedFields(dataSetInitializer.toTranslationFields(post.getTranslations()));

            stepDto.setPosts(Arrays.asList(postDto));

            // new steps
            StepCreationDto stepNewDto = new StepCreationDto();
            stepNewDto.setType(StepType.AUTO);
            stepNewDto.setIsDeletable(false);
            stepNewDto.setTranslatedFields(translationsMap("step", StepFieldsEnum.values()));

            PostCreationDto postNewDto = new PostCreationDto();
            postNewDto.setMeasureUnitId(dataset.measureUnit_mm2.getId());
            postNewDto.setIsDeletable(false);
            postNewDto.setTranslatedFields(translationsMap("post", PostFieldsEnum.values()));

            stepNewDto.setPosts(Arrays.asList(postNewDto));

            // update dto
            RoutingComponentCreateUpdateDto dto = new RoutingComponentCreateUpdateDto();
            dto.setOperationTypeId(rc.getOperationType().getId());
            dto.setInspectionValue(rc.getInspection().getValue());
            dto.setTaskId(rc.getFunctionality().getId());
            dto.setSubTaskId(rc.getDamageId());
            dto.setStatus(rciRoutingComponent.getStatus());
            dto.setSteps(Arrays.asList(stepDto, stepNewDto));

            try {
                routingComponentService.updateRoutingComponent(dto, rciRoutingComponent.getNaturalId(), validateComponent);
            } catch (FunctionalException e) {
                AssertionErrors.fail("Fail to update components, this should not happen :(");
            }
        });
    }

    private void updateTodoList(Boolean validateComponent) {
       runInTransaction(() -> {
           TodoList tl = rciTodoList.getTodoList();

           //  change current step
           Step step = tl.getSteps().get(0);
           StepCreationDto stepDto = new StepCreationDto();
           stepDto.setId(step.getNaturalId());
           stepDto.setType(step.getType());
           stepDto.setIsDeletable(false);
           stepDto.setTranslatedFields(dataSetInitializer.toTranslationFields(step.getTranslations()));
           stepDto.getTranslatedFields().get(Language.FR).put(StepFieldsEnum.information, "step updated FR");
           stepDto.getTranslatedFields().get(Language.EN).put(StepFieldsEnum.information, "step updated EN");

           RoutingComponentCreateUpdateDto dto = new RoutingComponentCreateUpdateDto();
           dto.setOperationTypeId(tl.getOperationType().getId());
           dto.setInspectionValue(tl.getInspection().getValue());
           dto.setTaskId(tl.getTodoListName().getId());
           dto.setStatus(rciTodoList.getStatus());
           dto.setSteps(Arrays.asList(stepDto));

           try {
               routingComponentService.updateRoutingComponent(dto, rciTodoList.getNaturalId(), validateComponent);
           } catch (FunctionalException e) {
               AssertionErrors.fail("Fail to update components, this should not happen :(");
           }
       });
    }

    private Map translationsMap (String name, Enum[] fields) {
        Map<Language, Map<Enum, String>> translationsFields = new HashMap<>();
        translationsFields.put(Language.EN, new HashMap<>());
        translationsFields.put(Language.FR, new HashMap<>());

        Stream.of(fields).forEach(field -> {
            translationsFields.get(Language.EN).put(field, name + " " + field.name() + " " + Language.EN.toString());
            translationsFields.get(Language.FR).put(field, name + " " + field.name() + " " + Language.FR.toString());
        });

        return translationsFields;
    };

    private Routing initializeRouting() {
        // todolist
        TodoList todoList = dataSetInitializer.createTodoList(tl -> {
            tl.setOperationType(dataset.operationType_closure);
        });
        rciTodoList = dataSetInitializer.createRoutingComponentIndex(null, todoList.getTechnicalId());
        Step stepTL = dataSetInitializer.createStep(s -> {
            s.setRoutingComponent(null);
            s.setTodoList(todoList);
            s.setStepNumber(1);
            s.setType(StepType.AUTO);
            s.setFiles(new HashSet<>());
        });
        todoList.addStep(stepTL);

        // routing component
        RoutingComponent rc = dataSetInitializer.createRoutingComponent(component -> {
            component.setOperationType(dataset.operationType_dimensional);
            component.setFunctionality(dataset.functionality_teeth);
        });

        dataset.functionality_teeth.setRoutingComponents(new HashSet<>(Arrays.asList(rc)));
        rciRoutingComponent = dataSetInitializer.createRoutingComponentIndex(rc.getTechnicalId(), null);
        Step step = dataSetInitializer.createStep(s -> {
            s.setRoutingComponent(rc);
            s.setTodoList(null);
            s.setStepNumber(1);
            s.setType(StepType.AUTO);
            s.setFiles(new HashSet<>());
        });
        Post post = dataSetInitializer.createPost("blabla FR", "blabla EN");
        step.addPost(post);
        rc.addStep(step);

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

        operationTL = dataSetInitializer.createOperation(1, ope -> {
            ope.setOperationType(dataset.operationType_preliminary);
            ope.setRouting(routing);
        });
        operationTL.addTodoList(todoList);
        routing.addOperation(operationTL);

        operation = dataSetInitializer.createOperation(1, ope -> {
            ope.setOperationType(dataset.operationType_dimensional);
            ope.setRouting(routing);
        });
        routing.addOperation(operation);

        OperationFunctionalArea operationFunctionalArea = dataSetInitializer.createOperationFunctionalArea(ofa -> {
            ofa.setOperation(operation);
            ofa.setFunctionalArea(functionalArea);
        });
        operation.addOperationFunctionalArea(operationFunctionalArea);

        StepActivation stepActivation = dataSetInitializer.createStepActivation((sa) -> {
            sa.setOperationFunctionalArea(operationFunctionalArea);
        }, true, step, operationFunctionalArea);
        step.addStepActivation(stepActivation);
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
