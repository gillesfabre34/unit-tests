package com.airbus.retex.routingComponent;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.post.PostCreationDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentCreateUpdateDto;
import com.airbus.retex.business.dto.step.StepCreationDto;
import com.airbus.retex.model.post.PostFieldsEnum;
import com.airbus.retex.model.step.StepFieldsEnum;
import com.airbus.retex.business.messages.CommonMessages;
import com.airbus.retex.dataset.DatasetInitializer;
import com.airbus.retex.helper.DatabaseVerificationService;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.common.StepType;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.persistence.routingComponent.RoutingComponentIndexRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class RoutingComponentControllerIT extends BaseControllerTest {
    private final static String API_ROUTING_COMPONENTS = API+ "/routing-components";
    private static final String API_ROUTING_COMPONENT =  API+"/routing-components/{id}";
    private static final String API_ROUTING_COMPONENTS_AVAILABLE_FILTERS = API_ROUTING_COMPONENTS + "/available-filters";
    private static final String API_DELETE_ROUTING_COMPONENT = API_ROUTING_COMPONENTS+"/{id}";
    private static final String API_ROUTING_COMPONENT_STEPS = API_ROUTING_COMPONENTS+ "/{id}/steps";

    @Autowired
    private DatabaseVerificationService databaseVerificationService;
    @Autowired
    private RoutingComponentIndexRepository routingComponentIndexRepository;
    @Autowired
    private DatasetInitializer datasetInitializer;
    @Autowired
    private CommonMessages commonMessages;
    @Autowired
    private ObjectMapper objectMapper;

    private Post measureUnitmm;
    private Post measureUnitmm2;

    private RoutingComponentIndex routingComponentIndex;
    private RoutingComponentIndex routingComponentIndexOne;
    private RoutingComponentIndex routingComponentIndexTwo;
    private RoutingComponentIndex routingComponentVisualIndex;
    private RoutingComponentIndex routingComponentPriliminaryIndex;

    @BeforeEach
    public void before() {
        runInTransaction(() -> {
            routingComponentIndex = datasetInitializer.createRoutingComponentIndex(dataset.routingComponent_visual.getTechnicalId(), null);
            routingComponentIndexOne = datasetInitializer.createRoutingComponentIndex(dataset.routingComponent_todo_list.getTechnicalId(), null);
            routingComponentIndexTwo = datasetInitializer.createRoutingComponentIndex(null, dataset.todoList_1.getTechnicalId());
            dataSetInitializer.createUserFeature(FeatureCode.ROUTING_COMPONENT, dataset.user_simpleUser, EnumRightLevel.WRITE);
            routingComponentVisualIndex =  createTypedRoutingComponentWithSteps(dataset.operationType_visual, 1);
            routingComponentPriliminaryIndex =  createTypedRoutingComponentWithSteps(dataset.operationType_preliminary, 1);

            datasetInitializer.createTodoListName(todoListName -> {
                todoListName.setOperationTypeId(dataset.operationType_preliminary.getId());
            });
        });
    }

    @Test
    public void getAllRoutingComponentsIndex_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_ROUTING_COMPONENTS);
        expectedStatus = HttpStatus.OK;
        abstractCheck();
    }

    @Test
    public void getAllRoutingComponentsIndex_Forbidden() throws Exception {
        asUser = dataset.user_simpleUser2;
        withRequest = get(API_ROUTING_COMPONENTS);
        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }

    @Test
    public void getAllRoutingComponentsIndex_OperationTypeFiltered_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_ROUTING_COMPONENTS);
        withParams.add("operationTypeId", dataset.operationType_dimensional.getId().toString());
        expectedStatus = HttpStatus.OK;
        abstractCheck();
    }

    @Test
    public void getAllFiltersRoutingComponents() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_ROUTING_COMPONENTS_AVAILABLE_FILTERS);
        expectedStatus = HttpStatus.OK;
        abstractCheck();
    }

    @Test
    public void getAllFiltersRoutingComponents_OperationTypeFiltered_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_ROUTING_COMPONENTS_AVAILABLE_FILTERS);
        withParams.add("operationTypeId", dataset.operationType_preliminary.getId().toString());
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$.todoListList").isNotEmpty())
                .andExpect(jsonPath("$.todoListList", hasSize(1)))
                .andExpect(jsonPath("$.statusList", hasSize(2)));
    }

    @Test
    public void getAllFiltersRoutingComponents_inspectionList_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_ROUTING_COMPONENTS_AVAILABLE_FILTERS);
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$.inspectionList", hasSize(3)));
    }

    @Test
    public void routingComponent_dimensional_creation_ok() throws Exception {
        asUser = dataset.user_superAdmin;
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_dimensional, 2);
        componentCreationDto.setSteps(createSteps(3));
        withRequest = post(API_ROUTING_COMPONENTS).param("publishing", "false").content(objectMapper.writeValueAsString(componentCreationDto));
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$.taskId").value(componentCreationDto.getTaskId()))
                .andExpect(jsonPath("$.subTaskId").value(componentCreationDto.getSubTaskId()))
                .andExpect(jsonPath("$.inspectionValue").value(dataset.inspection_external.getValue()))
                .andExpect(jsonPath("$.status").value(EnumStatus.CREATED.toString()));
    }

    @Test
    public void routingComponent_dimensional_creation_OperationType_null_BadRequest_ok() throws Exception {
        asUser = dataset.user_superAdmin;
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_dimensional, 3);
        // a nonexistent operation type
        componentCreationDto.setOperationTypeId(null);
        withRequest = post(API_ROUTING_COMPONENTS).content(objectMapper.writeValueAsString(componentCreationDto)).locale(Locale.FRENCH);
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck().andExpect(jsonPath("$.messages").value("The operation type not found"));
    }

    @Test
    public void routingComponent_visual_creation_ok() throws Exception {
        asUser = dataset.user_superAdmin;
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_visual, 1);
        withRequest = post(API_ROUTING_COMPONENTS).param("publishing", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        expectedStatus = HttpStatus.OK;
        MvcResult res = abstractCheck()
                .andExpect(jsonPath("$.taskId").value(componentCreationDto.getTaskId()))
                .andExpect(jsonPath("$.subTaskId").value(componentCreationDto.getSubTaskId()))
                .andExpect(jsonPath("$.inspectionValue").value(dataset.inspection_external.getValue()))
                .andExpect(jsonPath("$.status").value(EnumStatus.CREATED.toString())).andReturn();

        RoutingComponentHelperDto dto = null;

        try {
            dto = objectMapper.readValue(res.getResponse().getContentAsString(), RoutingComponentHelperDto.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //FIXME databaseVerificationService.retrieveOne(dto, true, false);
        Optional<RoutingComponentIndex> opt = routingComponentIndexRepository.findLastVersionByNaturalId(dto.getRoutingComponentIndexId());
        assertThat(opt.isPresent(), is(true));
    }

    @Test
    public void routingComponent_creation_ko_bad_operationType() throws Exception {
        asUser = dataset.user_superAdmin;
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_visual, 1);
        componentCreationDto.setOperationTypeId(-1L);
        withRequest = post(API_ROUTING_COMPONENTS).param("publishing", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck().andExpect(jsonPath("$.messages").value("The operation type not found"));
    }

    @Test
    public void routingComponent_creation_ko_bad_functionality() throws Exception {
        asUser = dataset.user_superAdmin;
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_visual, 1);
        componentCreationDto.setTaskId(-1L);
        withRequest = post(API_ROUTING_COMPONENTS).param("publishing", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck();
    }

    @Test
    public void routingComponent_creation_ko_bad_damage() throws Exception {
        asUser = dataset.user_superAdmin;
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_visual, 1);
        componentCreationDto.setSubTaskId(-1L);
        withRequest = post(API_ROUTING_COMPONENTS).param("publishing", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck();
    }

    @Test
    public void routingComponent_visual_creation_doublon_ko() throws Exception {
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_visual, 1);
        datasetInitializer.createRoutingComponent(routingComponent -> {
            routingComponent.setOperationType(dataset.operationType_visual);
            routingComponent.setFunctionality(dataset.functionality_teeth);
            routingComponent.setInspection(dataset.inspection_external);
            routingComponent.setDamageId(componentCreationDto.getSubTaskId());
        });
        asUser = dataset.user_superAdmin;
        withRequest = post(API_ROUTING_COMPONENTS).param("publishing", "true").content(objectMapper.writeValueAsString(componentCreationDto)).locale(Locale.FRENCH);
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck().andExpect(jsonPath("$.messages").value("Routing Component not unique"));
    }

    @Test
    public void routingComponent_visual_update_not_unique_ko() throws Exception {
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_visual, 1);
        datasetInitializer.createRoutingComponent(routingComponent -> {
            routingComponent.setOperationType(dataset.operationType_visual);
            routingComponent.setFunctionality(dataset.functionality_teeth);
            routingComponent.setInspection(dataset.inspection_external);
            routingComponent.setDamageId(componentCreationDto.getSubTaskId());
        });
        withRequest = put(API_ROUTING_COMPONENT, routingComponentIndex.getNaturalId()).param("publishing", "true").content(objectMapper.writeValueAsString(componentCreationDto)).locale(Locale.FRENCH);
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck().andExpect(jsonPath("$.messages").value("Routing Component not unique"));
    }

    @Test
    public void routingComponent_update_ko_routing_component_not_unique() throws Exception {
        asUser = dataset.user_superAdmin;
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_visual, 3);

        RoutingComponent r = datasetInitializer.createRoutingComponent(routingComponent -> {
            routingComponent.setOperationType(dataset.operationType_visual);
            routingComponent.setFunctionality(dataset.functionality_teeth);
            routingComponent.setInspection(dataset.inspection_external);
            routingComponent.setDamageId(componentCreationDto.getSubTaskId());
        });
        routingComponentIndex.setRoutingComponent(r);
        withRequest = put(API_ROUTING_COMPONENT, routingComponentIndex.getNaturalId()).param("publishing", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck().andExpect(jsonPath("$.messages").value("Routing Component not unique"));
    }

    @Test
    public void routingComponent_visual_update_routing_component_not_found_ko() throws Exception {
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_visual, 8);
        datasetInitializer.createRoutingComponent(routingComponent -> {
            routingComponent.setOperationType(dataset.operationType_visual);
            routingComponent.setFunctionality(dataset.functionality_teeth);
            routingComponent.setInspection(dataset.inspection_external);
            routingComponent.setDamageId(componentCreationDto.getSubTaskId());
        });
        withRequest = put(API_ROUTING_COMPONENT, -1L).param("publishing", "true").content(objectMapper.writeValueAsString(componentCreationDto)).locale(Locale.FRENCH);
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck().andExpect(jsonPath("$.messages").value("Routing Component index not found"));
    }

    @Test
    public void routingComponent_visual_update_ok() throws Exception {
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_visual, 1);
        RoutingComponent routingComponent1 = datasetInitializer.createRoutingComponent(rc -> {
            rc.setOperationType(dataset.operationType_visual);
            rc.setInspection(dataset.inspection_external);
        });
        RoutingComponentIndex routingComponentIndexToUpdate = datasetInitializer.createRoutingComponentIndex(routingComponent1.getTechnicalId(), null);

        withRequest = put(API_ROUTING_COMPONENT, routingComponentIndexToUpdate.getNaturalId()).param("validate", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$.taskId").value(componentCreationDto.getTaskId()))
                .andExpect(jsonPath("$.subTaskId").value(componentCreationDto.getSubTaskId()))
                .andExpect(jsonPath("$.inspectionValue").value(dataset.inspection_external.getValue()))
                .andExpect(jsonPath("$.status").value(EnumStatus.VALIDATED.toString()));
    }

    @Test
    public void todoList_preliminary_update_ok() throws Exception {
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_preliminary, 1);
        TodoList toDoListToUpdate = datasetInitializer.createTodoList(todoList -> {
            todoList.setOperationType(dataset.operationType_preliminary);
            todoList.setInspection(dataset.inspection_external);
        });
        RoutingComponentIndex routingComponentIndexToUpdate = datasetInitializer.createRoutingComponentIndex(null, toDoListToUpdate.getTechnicalId());

        withRequest = put(API_ROUTING_COMPONENT, routingComponentIndexToUpdate.getNaturalId()).param("validate", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$.taskId").value(componentCreationDto.getTaskId()))
                // subTask is null for RoutingComponent type
                .andExpect(jsonPath("$.subTaskId").value(is(nullValue())))
                .andExpect(jsonPath("$.inspectionValue").value(dataset.inspection_external.getValue()))
                .andExpect(jsonPath("$.status").value(EnumStatus.VALIDATED.toString()));
    }

    @Test
    public void routingComponent_dimensional_update_ok() throws Exception {
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_dimensional, 4);

        RoutingComponent routingComponentToUpdate = datasetInitializer.createRoutingComponent(routingComponent -> {
            routingComponent.setOperationType(dataset.operationType_dimensional);
            routingComponent.setFunctionality(dataset.functionality_teeth);
            routingComponent.setInspection(dataset.inspection_external);
            routingComponent.setDamageId(componentCreationDto.getSubTaskId());
        });

        RoutingComponentIndex routingComponentIndexToUpdate = datasetInitializer.createRoutingComponentIndex(routingComponentToUpdate.getTechnicalId(), null);

        measureUnitmm = datasetInitializer.createPost(post -> {
            post.setMeasureUnit(dataset.measureUnit_mm);
            post.setMeasureUnitId(dataset.measureUnit_mm.getId());
        }, "mm", "mm");

        measureUnitmm2 = datasetInitializer.createPost(post -> {
            post.setMeasureUnit(dataset.measureUnit_mm2);
            post.setMeasureUnitId(dataset.measureUnit_mm2.getId());
            post.setStep(dataset.step_two);
        }, "mm2", "mm2");

        PostCreationDto measureUnitDtomm2 = new PostCreationDto();
        measureUnitDtomm2.setMeasureUnitId(dataset.measureUnit_mm2.getId());
        measureUnitDtomm2.setId(measureUnitmm.getNaturalId());

        Map<Language, Map<PostFieldsEnum, String>> postsFields = new HashMap<>();
        Map<PostFieldsEnum, String> postsFieldsFR = new HashMap<>();
        postsFieldsFR.put(PostFieldsEnum.designation, "mm2");

        Map<PostFieldsEnum, String> postsFieldsEN = new HashMap<>();
        postsFieldsEN.put(PostFieldsEnum.designation, "mm2");

        postsFields.put(Language.FR, postsFieldsFR);
        postsFields.put(Language.EN, postsFieldsEN);

        measureUnitDtomm2.setTranslatedFields(postsFields);

        withRequest = put(API_ROUTING_COMPONENT, routingComponentIndexToUpdate.getNaturalId()).param("validate", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$.taskId").value(componentCreationDto.getTaskId()))
                .andExpect(jsonPath("$.subTaskId").value(componentCreationDto.getSubTaskId()))
                .andExpect(jsonPath("$.inspectionValue").value(dataset.inspection_external.getValue()))
                .andExpect(jsonPath("$.status").value(EnumStatus.VALIDATED.toString()));
    }


    @Test
    public void todolist_preliminary_creation_ok() throws Exception {
        asUser = dataset.user_superAdmin;
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_preliminary, 1);
        withRequest = post(API_ROUTING_COMPONENTS).param("validate", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$.taskId").value(componentCreationDto.getTaskId()))
                // subTask is null for RoutingComponent type
                .andExpect(jsonPath("$.subTaskId").value(is(nullValue())))
                .andExpect(jsonPath("$.inspectionValue").value(dataset.inspection_external.getValue()))
                .andExpect(jsonPath("$.status").value(EnumStatus.VALIDATED.toString()));
    }

    @Test
    public void todolist_preliminary_creation_ko_bad_operationType_() throws Exception {
        asUser = dataset.user_superAdmin;
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_preliminary, 1);
        componentCreationDto.setOperationTypeId(-1L);
        withRequest = post(API_ROUTING_COMPONENTS).param("validate", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck();
    }

    @Test
    public void todoList_update_ko_bad_routingComponentIndex() throws Exception {
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_preliminary, 1);
        TodoList toDoListToUpdate = datasetInitializer.createTodoList(todoList -> {
            todoList.setOperationType(dataset.operationType_preliminary);
            todoList.setInspection(dataset.inspection_external);
        });
        RoutingComponentIndex routingComponentIndexToUpdate = datasetInitializer.createRoutingComponentIndex(null, toDoListToUpdate.getTechnicalId());

        withRequest = put(API_ROUTING_COMPONENT, -1L).param("publishing", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck().andExpect(jsonPath("$.messages").value("Routing Component index not found"));
    }

    @Test
    public void todoList_update_ko_routing_component_not_found() throws Exception {
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_preliminary, 1);
        TodoList toDoListToUpdate = datasetInitializer.createTodoList(todoList -> {
            todoList.setOperationType(dataset.operationType_preliminary);
            todoList.setInspection(dataset.inspection_external);
        });
        RoutingComponentIndex routingComponentIndexToUpdate = datasetInitializer.createRoutingComponentIndex(null, toDoListToUpdate.getTechnicalId(), RoutingComponentIndex -> {
        });
        componentCreationDto.setOperationTypeId(-1L);

        withRequest = put(API_ROUTING_COMPONENT, routingComponentIndexToUpdate.getNaturalId()).param("publishing", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck().andExpect(jsonPath("$.messages").value("Routing Component not found"));
    }


    @Test
    public void routingComponent_update_ko_routing_component_not_found() throws Exception {
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_dimensional, 1);
        RoutingComponent routingComponentToUpdate = datasetInitializer.createRoutingComponent(routingComponent -> {
            routingComponent.setOperationType(dataset.operationType_dimensional);
            routingComponent.setFunctionality(dataset.functionality_teeth);
            routingComponent.setInspection(dataset.inspection_external);
            routingComponent.setDamageId(componentCreationDto.getSubTaskId());
        });
        RoutingComponentIndex routingComponentIndexToUpdate = datasetInitializer.createRoutingComponentIndex(routingComponentToUpdate.getTechnicalId(), null);
        componentCreationDto.setOperationTypeId(-1L);

        withRequest = put(API_ROUTING_COMPONENT, routingComponentIndexToUpdate.getNaturalId()).param("publishing", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck().andExpect(jsonPath("$.messages").value("Routing Component not found"));
    }

    @Test
    public void todolist_preliminary_creation_ko_bad_todolistName_() throws Exception {
        asUser = dataset.user_superAdmin;
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_preliminary, 1);
        componentCreationDto.setTaskId(-1L);
        withRequest = post(API_ROUTING_COMPONENTS).param("publishing", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck();
    }

    @Test
    public void todolist_preliminary_creation_ko_bad_inspection_() throws Exception {
        asUser = dataset.user_superAdmin;
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_preliminary, 1);
        componentCreationDto.setInspectionValue("");
        withRequest = post(API_ROUTING_COMPONENTS).param("publishing", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck().andExpect(jsonPath("$.messages").value("The Inspection not exists"));
    }

    @Test
    public void routingComponent_creation_ko_bad_inspection_() throws Exception {
        asUser = dataset.user_superAdmin;
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_visual, 2);
        componentCreationDto.setInspectionValue("");
        withRequest = post(API_ROUTING_COMPONENTS).param("publishing", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck().andExpect(jsonPath("$.messages").value("The Inspection not exists"));
    }

    @Test
    public void routingComponent_undefined_creation_ok() throws Exception {
        asUser = dataset.user_superAdmin;
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_generic_undefined, 1);
        componentCreationDto.setSubTaskId(null);
        componentCreationDto.setTaskId(null);
        componentCreationDto.setInspectionValue(dataset.inspection_generic.getValue());
        withRequest = post(API_ROUTING_COMPONENTS).param("validate", "false").content(objectMapper.writeValueAsString(componentCreationDto));
        expectedStatus = HttpStatus.OK;
        MvcResult res = abstractCheck()
                // task should be null for RoutingComponent type UNDEFINED
                .andExpect(jsonPath("$.taskId").value(is(nullValue())))
                // subTask is null for RoutingComponent type UNDEFINED
                .andExpect(jsonPath("$.subTaskId").value(is(nullValue())))
                // inspection is null for RoutingComponent type UNDEFINED
                .andExpect(jsonPath("$.inspectionValue").value(is(nullValue())))
                .andExpect(jsonPath("$.status").value(EnumStatus.CREATED.toString())).andReturn();

        RoutingComponentHelperDto dto = null;

        try {
            dto = objectMapper.readValue(res.getResponse().getContentAsString(), RoutingComponentHelperDto.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //FIXME Use databaseVerificationService.retrieveOne(dto, true, false);
        Optional<RoutingComponentIndex> opt = routingComponentIndexRepository.findLastVersionByNaturalId(dto.getRoutingComponentIndexId());
        assertThat(opt.isPresent(), is(true));
    }

    @Test
    public void routingComponent_undefined_update_ok() throws Exception {
        RoutingComponentCreateUpdateDto componentCreationDto = createRoutingComponentDto(dataset.operationType_generic_undefined, 2);
        RoutingComponent routingComponentToUpdate = datasetInitializer.createRoutingComponent(routingComponent -> {
            routingComponent.setOperationType(dataset.operationType_generic_undefined);
            routingComponent.setFunctionality(null);
            routingComponent.setDamageId(null);
            routingComponent.setInspection(dataset.inspection_generic);
        });
        RoutingComponentIndex routingComponentIndexToUpdate = datasetInitializer.createRoutingComponentIndex(routingComponentToUpdate.getTechnicalId(), null);

        withRequest = put(API_ROUTING_COMPONENT, routingComponentIndexToUpdate.getNaturalId()).param("validate", "true").content(objectMapper.writeValueAsString(componentCreationDto));
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$.taskId").value(is(nullValue())))
                .andExpect(jsonPath("$.subTaskId").value(is(nullValue())))
                .andExpect(jsonPath("$.inspectionValue").value(is(nullValue())))
                .andExpect(jsonPath("$.status").value(EnumStatus.VALIDATED.toString()));
    }

    @Test
    public void deleteRoutingComponentValidatedOK() throws Exception {
        asUser = dataset.user_superAdmin;
        RoutingComponent routingComponent = datasetInitializer.createRoutingComponent();
        RoutingComponentIndex routingComponentIndex = datasetInitializer.createRoutingComponentIndex(routingComponent.getTechnicalId(), null, routingComponentIndex1 -> routingComponentIndex1.setStatus(EnumStatus.VALIDATED));

        withRequest = delete(API_DELETE_ROUTING_COMPONENT, routingComponentIndex.getNaturalId());
        expectedStatus = HttpStatus.NO_CONTENT;
        abstractCheck();
    }

    @Test
    public void deleteRoutingComponentValidatedKoLinkedToRouting() throws Exception {
        asUser = dataset.user_superAdmin;
        RoutingComponent routingComponent = datasetInitializer.createRoutingComponent();
        Step step = datasetInitializer.createStep(step1 -> step1.setRoutingComponent(routingComponent));
        RoutingComponentIndex routingComponentIndex = datasetInitializer.createRoutingComponentIndex(routingComponent.getTechnicalId(), null, routingComponentIndex1 -> routingComponentIndex1.setStatus(EnumStatus.VALIDATED));

        runInTransaction(() -> {
            Routing routing = datasetInitializer.createRouting(datasetInitializer.createPart(null));
            Operation operation = datasetInitializer.createOperation(1);
            routing.addOperation(operation);
            OperationFunctionalArea operationFunctionalArea = datasetInitializer.createOperationFunctionalArea(operationFunctionalArea1 -> {
                operationFunctionalArea1.setOperation(operation);
                operationFunctionalArea1.setFunctionalArea(dataset.functionalArea_1);

            });
            datasetInitializer.createStepActivation(true, step, operationFunctionalArea);
        });

        withRequest = delete(API_DELETE_ROUTING_COMPONENT, routingComponentIndex.getNaturalId());
        expectedStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        abstractCheck();
    }

    @Test
    public void deleteRoutingComponentOk() throws Exception {

        RoutingComponent routingComponent = datasetInitializer.createRoutingComponent();
        RoutingComponentIndex routingComponentIndex = datasetInitializer.createRoutingComponentIndex(routingComponent.getTechnicalId(), null, routingComponentIndex1 -> routingComponentIndex1.setStatus(EnumStatus.CREATED));
        asUser = dataset.user_superAdmin;
        withRequest = delete(API_DELETE_ROUTING_COMPONENT, routingComponentIndex.getNaturalId());
        expectedStatus = HttpStatus.NO_CONTENT;
        abstractCheck();
    }

    @Test
    public void deleteRoutingComponentForbidden() throws Exception {
        asUser = dataset.user_simpleUser2;
        withRequest = delete(API_DELETE_ROUTING_COMPONENT, routingComponentIndexTwo.getNaturalId());
        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }

    @Test
    public void deleteRoutingComponentIdNotExist() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = delete(API_DELETE_ROUTING_COMPONENT, 0);
        expectedStatus = HttpStatus.NOT_FOUND;
        abstractCheck();
    }

    @Test
    public void getStepsOfRoutingComponent_ok() throws Exception {
        asUser = dataset.user_simpleUser;

        AtomicReference<RoutingComponentIndex> routingComponentIndex = new AtomicReference<>();

        runInTransaction(() -> {
            RoutingComponent routingComponent = datasetInitializer.createRoutingComponent(rc -> {
                rc.setOperationType(dataset.operationType_dimensional);
            });
            Step newStep1 = datasetInitializer.createStep(s -> {
                s.setStepNumber(1);
                s.addFile(datasetInitializer.createMedia());
            });
            Step newStep2 = datasetInitializer.createStep(s -> {
                s.setStepNumber(2);
                s.addFile(datasetInitializer.createMedia());
            });
            routingComponent.addStep(newStep1);
            routingComponent.addStep(newStep2);

            routingComponentIndex.set(datasetInitializer.createRoutingComponentIndex(routingComponent.getTechnicalId(), null));
        });

        withRequest = get(API_ROUTING_COMPONENT_STEPS, routingComponentIndex.get().getNaturalId());
        expectedStatus = HttpStatus.OK;
        abstractCheckSteps(2);
    }

    @Test
    public void getStepsOfRoutingComponent_visual_ok() throws Exception {
        asUser = dataset.user_simpleUser;

        RoutingComponentIndex routingComponentIndex = datasetInitializer.createRoutingComponentIndex(dataset.routingComponent_visual.getTechnicalId(), null);

        withRequest = get(API_ROUTING_COMPONENT_STEPS, routingComponentIndex.getNaturalId());
        expectedStatus = HttpStatus.OK;
        abstractCheckSteps(1);
    }

    @Test
    public void getStepsOfRoutingComponent_todolist_ok() throws Exception {
        asUser = dataset.user_simpleUser;
        dataset.routingComponent_todo_list.setOperationType(dataset.operationType_todolist);
        RoutingComponentIndex routingComponentIndex = datasetInitializer.createRoutingComponentIndex(dataset.routingComponent_todo_list.getTechnicalId(), null);


        withRequest = get(API_ROUTING_COMPONENT_STEPS, routingComponentIndex.getNaturalId());
        expectedStatus = HttpStatus.OK;
        abstractCheckSteps(1);
    }

    @Test
    public void routingComponent_visual_nb_steps_greater_than_one_BadRequest() throws Exception {
        // a visual routing component can't have more than one step
        asUser = dataset.user_simpleUser;
        RoutingComponentIndex routingComponentVisualIndex= createTypedRoutingComponentWithSteps(dataset.operationType_visual, 2);
        withRequest = get(API_ROUTING_COMPONENT_STEPS, routingComponentVisualIndex.getNaturalId());
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck();
    }

    @Test
    public void routingComponent_priliminary_nb_steps_greater_than_one_BadRequest() throws Exception {
        // a priliminary routing component can't have more than one step
        RoutingComponentIndex routingComponentVisualIndex= createTypedRoutingComponentWithSteps(dataset.operationType_preliminary, 2);
        asUser = dataset.user_simpleUser;
        withRequest = get(API_ROUTING_COMPONENT_STEPS, routingComponentVisualIndex.getNaturalId());
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck();
    }

    // -----------------------------------------------------------------------

    private void abstractCheckSteps(int expectedNbSteps) throws Exception {
        abstractCheck()
                .andExpect(jsonPath("$", hasSize(expectedNbSteps)))
                .andExpect(jsonPath("$.[*].id", notNullValue()))
                .andExpect(jsonPath("$.[*].information", notNullValue()))
                .andExpect(jsonPath("$.[*].name", notNullValue()))
                .andExpect(jsonPath("$.[*].files").isNotEmpty())
                .andExpect(jsonPath("$.[*].posts").isArray());
    }

    private RoutingComponentCreateUpdateDto createRoutingComponentDto(OperationType operationType, int nbStep) {
        RoutingComponentCreateUpdateDto routingComponentCreation = new RoutingComponentCreateUpdateDto();
        routingComponentCreation.setInspectionValue(dataset.inspection_external.getValue());
        routingComponentCreation.setOperationTypeId(operationType.getId());
        routingComponentCreation.setTaskId(dataset.functionality_teeth.getId());
        routingComponentCreation.setSubTaskId(dataset.damage_corrosion.getTechnicalId());

        if (nbStep != 0) {
            routingComponentCreation.setSteps(createSteps(nbStep));
        }
        return routingComponentCreation;
    }

    List<StepCreationDto> createSteps(int nbSteps) {
        List<StepCreationDto> steps = new ArrayList<>();
        IntStream.range(0, nbSteps).forEach(e ->
                steps.add(createStepCreationDto("Information step " + e, "name step " + e,
                        "Step's informations " + e, "Step's name" + e,
                        Arrays.asList(datasetInitializer.createTemporaryMedia().getUuid().toString())))
        );
        return steps;
    }

    StepCreationDto createStepCreationDto(String informationsFR, String nameFR,
                                          String informationsEN, String nameEN, List<String> files) {
        StepCreationDto stepCreationDto = new StepCreationDto();
        stepCreationDto.setType(StepType.AUTO);
        stepCreationDto.setMediaUuids(files);
        // set translated Fields
        Map<Language, Map<StepFieldsEnum, String>> stepsFields = new HashMap<>();
        Map<StepFieldsEnum, String> stepsFieldsFR = new HashMap<>();
        stepsFieldsFR.put(StepFieldsEnum.information, informationsFR);
        stepsFieldsFR.put(StepFieldsEnum.name, nameFR);
        Map<StepFieldsEnum, String> stepsFieldsEN = new HashMap<>();
        stepsFieldsFR.put(StepFieldsEnum.information, informationsEN);
        stepsFieldsFR.put(StepFieldsEnum.name, nameEN);
        stepsFields.put(Language.FR, stepsFieldsFR);
        stepsFields.put(Language.EN, stepsFieldsEN);
        stepCreationDto.setTranslatedFields(stepsFields);
        // set step's posts
        stepCreationDto.setPosts(Arrays.asList(createPosts("Désignation en français", "Designation on english")));
        return stepCreationDto;
    }

    PostCreationDto createPosts(String designationFR, String designationEN) {
        PostCreationDto postCreationDto = new PostCreationDto();
        postCreationDto.setMeasureUnitId(dataset.measureUnit_mm.getId());
        Map<Language, Map<PostFieldsEnum, String>> postsFields = new HashMap<>();
        Map<PostFieldsEnum, String> postsFieldsFR = new HashMap<>();
        postsFieldsFR.put(PostFieldsEnum.designation, designationFR);
        Map<PostFieldsEnum, String> postsFieldsEN = new HashMap<>();
        postsFieldsEN.put(PostFieldsEnum.designation, designationEN);
        postsFields.put(Language.FR, postsFieldsFR);
        postsFields.put(Language.EN, postsFieldsEN);
        postCreationDto.setTranslatedFields(postsFields);
        return postCreationDto;
    }

    RoutingComponentIndex createTypedRoutingComponentWithSteps(OperationType operationType, int nbOperations){
        RoutingComponent typedRoutingComponent = datasetInitializer.createRoutingComponent(rc ->{
            rc.setOperationType(operationType);
        });
        RoutingComponentIndex typedRoutingComponentPriliminaryIndex = datasetInitializer.createRoutingComponentIndex(typedRoutingComponent.getTechnicalId(), null);
        for(int i=0; i<nbOperations;i++){
            datasetInitializer.createStep(step -> {
                step.setRoutingComponent(typedRoutingComponent);
            });
            datasetInitializer.createStep(step -> {
                step.setRoutingComponent(typedRoutingComponent);
            });
        }
        return typedRoutingComponentPriliminaryIndex;
    }

}
