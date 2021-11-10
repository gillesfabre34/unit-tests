package com.airbus.retex.routing;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.routing.Routing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class RoutingControllerGetInspectionHeaderIT extends BaseControllerTest {
    private Part part;
    private Operation operation;
    private FunctionalArea functionalArea;
    private Routing routingOne;

    @BeforeEach
    public void before() {
        runInTransaction(() -> {
            part = dataSetInitializer.createPart(part1 -> part1.setPartNumber("9999999"),null);
            functionalArea = dataSetInitializer.createFunctionalArea(functionalArea -> {
                functionalArea.setPart(part);
                functionalArea.setFunctionality(dataset.functionality_teeth);
            });
            routingOne = dataSetInitializer.createRouting(part);

            operation = dataSetInitializer.createOperation(1, operation1 -> {
                operation1.setRouting(routingOne);
            });
            dataSetInitializer.createOperationFunctionalArea(operationFunctionalArea -> {
                operationFunctionalArea.setFunctionalArea(functionalArea);
                operationFunctionalArea.setOperation(operation);
            });

            dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.WRITE);
        });
    }

    @Test
    public void get_routing_inspection_header_ok() throws Exception {

        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_simpleUser;
        routingOne.addOperation(operation);
        operation.addOperationFunctionalArea(dataSetInitializer.createOperationFunctionalArea((ofa) -> {
            ofa.setFunctionalArea(functionalArea);
        }));
        withRequest = get("/api/routings/" + routingOne.getNaturalId() + "/operations/" + operation.getNaturalId() + "/tasks/" + functionalArea.getNaturalId() + "/header");

        abstractCheck()
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.areaNumber").value(functionalArea.getAreaNumber()))
                .andExpect(jsonPath("$.functionalAreaName").isMap())
                .andExpect(jsonPath("$.functionalAreaName.id").value(dataset.functionalAreaName_OuterRingBottom.getId()))
                .andExpect(jsonPath("$.functionality").isMap())
                .andExpect(jsonPath("$.functionality.id").value(dataset.functionality_teeth.getId()))
                .andExpect(jsonPath("$.material").value(dataset.material_15CN6.getId()))
                .andExpect(jsonPath("$.treatment").isMap())
                .andExpect(jsonPath("$.treatment.id").value(dataset.treatment_cadmiumPlating.getId()))
                .andExpect(jsonPath("$.disabled").value(false));
    }

    @Test
    public void get_routing_inspection_header_ko_functional_area_not_exist() throws Exception {

        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;

        routingOne.addOperation(operation);

        withRequest = get("/api/routings/" + routingOne.getNaturalId() + "/operations/" + operation.getNaturalId() + "/tasks/" + -1 + "/header");
        abstractCheck()
                .andExpect(jsonPath("messages").value("Functional Area not found"));
    }

    @Test
    public void get_routing_inspection_header_ko_operation_not_exist() throws Exception {

        expectedStatus = HttpStatus.NOT_FOUND;
        asUser = dataset.user_simpleUser;

        withRequest = get("/api/routings/" + routingOne.getNaturalId() + "/operations/" + -1 + "/tasks/" + dataset.functionality_teeth.getId() + "/header");
        abstractCheck()
                .andExpect(jsonPath("messages").value("Operation not found"));
    }

    @Test
    public void get_routing_inspection_header_ko_forbidden() throws Exception {

        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_simpleUser2;

        withRequest = get("/api/routings/" + routingOne.getNaturalId() + "/operations/" + operation.getNaturalId() + "/tasks/" + dataset.functionality_teeth.getId() + "/header");
        abstractCheck();
    }

    @Test
    public void get_routing_inspection_header_ko_operation_type_not_allows() throws Exception {

        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;
        Operation operationTodoList = dataSetInitializer.createOperation(1, operation1 -> {
            operation1.setOperationType(dataset.operationType_preliminary);
            operation1.setRouting(routingOne);
        });
        routingOne.addOperation(operationTodoList);

        withRequest = get("/api/routings/" + routingOne.getNaturalId() + "/operations/" + operationTodoList.getNaturalId() + "/tasks/" + dataset.functionality_teeth.getId() + "/header");
        abstractCheck()
                .andExpect(jsonPath("messages").value("Operation type not allows"));
    }
}
