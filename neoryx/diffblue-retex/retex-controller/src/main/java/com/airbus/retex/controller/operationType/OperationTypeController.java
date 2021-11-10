package com.airbus.retex.controller.operationType;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.airbus.retex.business.dto.operationType.OperationTypeDto;
import com.airbus.retex.business.dto.todoList.TodoListDto;
import com.airbus.retex.configuration.CustomLocalResolver;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.service.operationType.IOperationTypeService;
import com.airbus.retex.service.todoList.ITodoListService;
import com.airbus.retex.utils.ConstantUrl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Operation Type", tags = {"Operation Type"})
@RestController
public class OperationTypeController {
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private IOperationTypeService operationTypeService;

    @Autowired
    private ITodoListService todoListService;

    @Autowired
    private CustomLocalResolver customLocalResolver;

    @ApiOperation("Get list of Operation Types")
    @GetMapping(ConstantUrl.API_OPERATION_TYPES)
    @Secured("ROLE_ROUTING:READ")
    public ResponseEntity<List<OperationTypeDto>> getAllOperationTypes() {
        return ResponseEntity.ok().body(operationTypeService.findOperationTypes());
    }

    @ApiOperation("Get list of Todo List For One Operation Types")
    @GetMapping(ConstantUrl.API_OPERATION_TYPES_TODO_LIST)
    @Secured("ROLE_ROUTING:READ")
    public ResponseEntity<List<TodoListDto>> getAllTodoListByOperationTypesId(@PathVariable Long id) {
        Locale locale = customLocalResolver.resolveLocale(httpServletRequest);

        return ResponseEntity.ok()
                .body(todoListService.findTodoListByOperationTypesId(id, Language.languageFor(locale)));
    }
}
