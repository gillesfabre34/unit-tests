package com.airbus.retex.operationType;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.persistence.operationType.OperationTypeRepository;
import com.airbus.retex.utils.ConstantUrl;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Collection;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class OperationTypeControllerIT extends BaseControllerTest {

    @Autowired
    private OperationTypeRepository operationTypeRepository;

    @Test
    public void getAllOperationTypesOK() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.WRITE);

        expectedStatus = HttpStatus.OK;

        checkGetAllOperationType(hasSize((int) operationTypeRepository.count()));
    }

    @Test
    public void getAllOperationTypesRead() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.READ);
        withRequest = get(ConstantUrl.API_OPERATION_TYPES);
        expectedStatus = HttpStatus.OK;

        abstractCheck();
    }

    @Test
    public void getAllOperationTypesNoFeature() throws Exception {
        asUser = dataset.user_superAdmin;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_superAdmin, EnumRightLevel.NONE);
        withRequest = get(ConstantUrl.API_OPERATION_TYPES);
        expectedStatus = HttpStatus.FORBIDDEN;

        abstractCheck();
    }

    @Test
    public void getAllTodoListByOperationType() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.WRITE);

        withRequest = get(ConstantUrl.API_OPERATION_TYPES_TODO_LIST,dataset.operationType_preliminary.getId());

        expectedStatus = HttpStatus.OK;

        abstractCheck()
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].todoListName").isNotEmpty())
                .andExpect(jsonPath("$[0].todoListName.name").isString());

    }

    @Test
    public void getAllTodoListByOperationTypeNoFeature() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.NONE);

        withRequest = get(ConstantUrl.API_OPERATION_TYPES_TODO_LIST,dataset.operationType_preliminary.getId());

        expectedStatus = HttpStatus.FORBIDDEN;

        abstractCheck();
    }

    private void checkGetAllOperationType(Matcher<? extends Collection> resultsMatcher) throws Exception {
        withRequest = get(ConstantUrl.API_OPERATION_TYPES);

        abstractCheck()
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", resultsMatcher))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].template").isString())
                .andExpect(jsonPath("$[0].behavior").isString());
    }
}
