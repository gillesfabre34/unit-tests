package com.airbus.retex.routingComponent;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.messages.CommonMessages;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.todoList.TodoList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class GetRoutingComponentControllerIT extends BaseControllerTest {
    private final static String API_ROUTING_COMPONENTS = "/api/routing-components";
    private static final String API_ROUTING_COMPONENT = "/api/routing-components/{id}";
    private static final String OPERATION_VISUAL_NAME = "Visual";
    private static final String OPERATION_DIMENSIONAL_NAME = "Dimensional";
    private static final String OPERATION_TRIDIMENSIONAL_NAME = "TriDimensional";
    private static final String OPERATION_LABORATORY_NAME = "Laboratory";
    private static final String OPERATION_PRELIMINARY_NAME = "Preliminary";
    private static final String OPERATION_CLOSURE_NAME = "Closure";
    private static final String OPERATION_UNDEFINED_NAME = "Generic";

    private RoutingComponent routingComponentDimensional;
    private RoutingComponent routingComponentTriDimensional;
    private RoutingComponent routingComponentLaboratory;
    private RoutingComponent routingComponentVisual;

    private TodoList todoListPreliminary;
    private TodoList todoListClosure;

    private RoutingComponent routingComponentUndefined;

    private RoutingComponentIndex rci_Dimensional;
    private RoutingComponentIndex rci_TriDimensional;
    private RoutingComponentIndex rci_Laboratory;
    private RoutingComponentIndex rci_Visual;
    private RoutingComponentIndex rci_Preliminary;
    private RoutingComponentIndex rci_Closure;
    private RoutingComponentIndex rci_Undefined;

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CommonMessages commonMessages;

    @BeforeEach
    public void before() {
        runInTransaction(() -> {
            routingComponentVisual = dataSetInitializer.createRoutingComponent();
            rci_Visual = dataSetInitializer.createRoutingComponentIndex(routingComponentVisual.getTechnicalId(), null);

            routingComponentTriDimensional = dataSetInitializer.createRoutingComponent(rc -> {
                rc.setOperationType(dataset.operationType_tridimensional);
                rc.setFunctionality(dataset.functionality_grooves);
                rc.setInspection(dataset.inspection_external);
            });
            rci_TriDimensional = dataSetInitializer.createRoutingComponentIndex(routingComponentTriDimensional.getTechnicalId(), null);

            routingComponentLaboratory = dataSetInitializer.createRoutingComponent(rc -> {
                rc.setOperationType(dataset.operationType_laboratory);
                rc.setFunctionality(dataset.functionality_splines);
                rc.setInspection(dataset.inspection_internal);
            });
            rci_Laboratory = dataSetInitializer.createRoutingComponentIndex(routingComponentLaboratory.getTechnicalId(), null);

            routingComponentDimensional = dataSetInitializer.createRoutingComponent(rc -> {
                rc.setOperationType(dataset.operationType_dimensional);
                rc.setFunctionality(dataset.functionality_splines);
                rc.setInspection(dataset.inspection_external);
            });
            rci_Dimensional = dataSetInitializer.createRoutingComponentIndex(routingComponentDimensional.getTechnicalId(), null);

            routingComponentUndefined = dataSetInitializer.createRoutingComponent(routingComponent -> {
                routingComponent.setDamageId(null);
                routingComponent.setOperationType(dataset.operationType_generic_undefined);
                routingComponent.setFunctionality(null);
                routingComponent.setInspection(dataset.inspection_generic);

            });
            rci_Undefined = dataSetInitializer.createRoutingComponentIndex(routingComponentUndefined.getTechnicalId(), null);

            todoListPreliminary = dataSetInitializer.createTodoList(todoList -> {
                todoList.setOperationType(dataset.operationType_preliminary);
                todoList.setInspection(dataset.inspection_generic);
            });
            rci_Preliminary = dataSetInitializer.createRoutingComponentIndex(null, todoListPreliminary.getTechnicalId());

            todoListClosure = dataSetInitializer.createTodoList(todoList -> {
                todoList.setOperationType(dataset.operationType_closure);
                todoList.setInspection(dataset.inspection_generic);
            });
            rci_Closure = dataSetInitializer.createRoutingComponentIndex(null, todoListClosure.getTechnicalId());
            dataSetInitializer.createUserFeature(FeatureCode.ROUTING_COMPONENT, dataset.user_simpleUser, EnumRightLevel.WRITE);
        });
    }


    private ResultActions checkGetRC(OperationType operationType, String operationTypeName, String inspectionValue, Long taskId, Long subTaskId, String status, String informationsEN, String informationsFR) throws Exception {
        return abstractCheck()
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.operationType").isMap())
                .andExpect(jsonPath("$.operationType.id").value(operationType.getId()))
                .andExpect(jsonPath("$.operationType.template").value(operationType.getTemplate()))
                .andExpect(jsonPath("$.operationType.name").value(operationTypeName))
                .andExpect(jsonPath("$.inspectionValue").value(inspectionValue))
                .andExpect(jsonPath("$.creationDate", notNullValue()))
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.subTaskId").value(subTaskId))
                .andExpect(jsonPath("$.status").value(status));
    }

    @Test
    public void getRoutingComponent_Forbidden() throws Exception {
        asUser = dataset.user_simpleUser2;
        withRequest = get(API_ROUTING_COMPONENT, rci_Visual.getNaturalId());
        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }

    @Test
    public void getRoutingComponent_BadRequest() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_ROUTING_COMPONENT, 0);
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck().andExpect(jsonPath("$.messages").value("The Routing Component not exists"));
    }

    @Test
    public void getRoutingComponent_Visual_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        routingComponentVisual.setOperationType(dataset.operationType_visual);
        withRequest = get(API_ROUTING_COMPONENT, rci_Visual.getNaturalId());
        expectedStatus = HttpStatus.OK;
        checkGetRC(dataset.operationType_visual,
                OPERATION_VISUAL_NAME,
                dataset.inspection_internal.getValue(),
                dataset.functionality_bearingRaces.getId(),
                dataset.damage_corrosion.getTechnicalId(),
                EnumStatus.VALIDATED.toString(),
                "Routing Component informations number " + routingComponentVisual.getNaturalId(),
                "Composant gamme informations numéro " + routingComponentVisual.getNaturalId());
    }

    @Test
    public void getRoutingComponent_Dimensional_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_ROUTING_COMPONENT, rci_Dimensional.getNaturalId());
        expectedStatus = HttpStatus.OK;
        checkGetRC(dataset.operationType_dimensional,
                OPERATION_DIMENSIONAL_NAME,
                dataset.inspection_external.getValue(),
                dataset.functionality_splines.getId(),
                dataset.damage_corrosion.getTechnicalId(),
                EnumStatus.VALIDATED.toString(),
                "Routing Component informations number " + routingComponentDimensional.getNaturalId(),
                "Composant gamme informations numéro " + routingComponentDimensional.getNaturalId());
    }

    @Test
    public void getRoutingComponent_TriDimensional_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_ROUTING_COMPONENT, rci_TriDimensional.getNaturalId());
        expectedStatus = HttpStatus.OK;
        checkGetRC(dataset.operationType_tridimensional,
                OPERATION_TRIDIMENSIONAL_NAME,
                dataset.inspection_external.getValue(),
                dataset.functionality_grooves.getId(),
                dataset.damage_corrosion.getTechnicalId(),
                EnumStatus.VALIDATED.toString(),
                "Routing Component informations number " + routingComponentTriDimensional.getNaturalId(),
                "Composant gamme informations numéro " + routingComponentTriDimensional.getNaturalId());
    }

    @Test
    public void getRoutingComponent_Laboratory_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_ROUTING_COMPONENT, rci_Laboratory.getNaturalId());
        expectedStatus = HttpStatus.OK;
        checkGetRC(dataset.operationType_laboratory,
                OPERATION_LABORATORY_NAME,
                dataset.inspection_internal.getValue(),
                dataset.functionality_splines.getId(),
                dataset.damage_corrosion.getTechnicalId(),
                EnumStatus.VALIDATED.toString(),
                "Routing Component informations number " + routingComponentLaboratory.getNaturalId(),
                "Composant gamme informations numéro " + routingComponentLaboratory.getNaturalId());
    }

    @Test
    public void getRoutingComponent_Generic_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_ROUTING_COMPONENT, rci_Undefined.getNaturalId());
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.operationType").isMap())
                .andExpect(jsonPath("$.operationType.id").value(dataset.operationType_generic_undefined.getId()))
                .andExpect(jsonPath("$.operationType.template").value(dataset.operationType_generic_undefined.getTemplate()))
                .andExpect(jsonPath("$.inspectionValue", nullValue()))
                .andExpect(jsonPath("$.creationDate", notNullValue()))
                .andExpect(jsonPath("$.taskId", nullValue()))
                .andExpect(jsonPath("$.subTaskId", nullValue()))
                .andExpect(jsonPath("$.status").value(EnumStatus.VALIDATED.toString()));
    }

    @Test
    public void getTodoList_Closure_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_ROUTING_COMPONENT, rci_Closure.getNaturalId());
        expectedStatus = HttpStatus.OK;
        checkGetRC(dataset.operationType_closure,
                OPERATION_CLOSURE_NAME,
                dataset.inspection_generic.getValue(),
                dataset.todoListName_1.getId(),
                null,
                EnumStatus.VALIDATED.toString(),
                "TodoList informations number " + todoListClosure.getNaturalId(),
                "TodoList informations numéro " + todoListClosure.getNaturalId());
    }

    @Test
    public void getTodoList_Preliminary_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_ROUTING_COMPONENT, rci_Preliminary.getNaturalId());
        expectedStatus = HttpStatus.OK;
        checkGetRC(dataset.operationType_preliminary,
                OPERATION_PRELIMINARY_NAME,
                dataset.inspection_generic.getValue(),
                dataset.todoListName_1.getId(),
                null,
                EnumStatus.VALIDATED.toString(),
                "TodoList informations number " + todoListPreliminary.getNaturalId(),
                "TodoList informations numéro " + todoListPreliminary.getNaturalId());
    }
}
