package com.airbus.retex.service.impl.todoList;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.todoList.TodoListDto;
import com.airbus.retex.business.dto.todoListName.TodoListNameDto;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.persistence.routingComponent.RoutingComponentIndexRepository;
import com.airbus.retex.persistence.todoList.TodoListRepository;
import com.airbus.retex.service.todoList.ITodoListService;
import com.airbus.retex.service.translate.ITranslateService;

@Service
@Transactional(rollbackFor = Exception.class)
public class TodoListServiceImpl implements ITodoListService {


    @Autowired
    private TodoListRepository todoListRepository;
    @Autowired
    private ITranslateService translateService;
    @Autowired
    private RoutingComponentIndexRepository routingComponentIndexRepository;

    @Autowired
    private DtoConverter dtoConverter;

    @Override
    public List<TodoListDto> findTodoListByOperationTypesId(Long operationTypeId, Language language) {
        Set<RoutingComponentIndex> todoLists =  routingComponentIndexRepository.findTodoListByOperationTypeIdAndStatusIsValidated(operationTypeId);
        List<TodoListDto> dtos = dtoConverter.toDtos(todoLists, TodoListDto.class);
        for(RoutingComponentIndex routingComponentIndex : todoLists) {
            for (TodoListDto todoListDto: dtos){
                if(todoListDto.getId().equals(routingComponentIndex.getNaturalId())){
                    todoListDto.setTodoListName(dtoConverter.toDto(routingComponentIndex.getTodoList().getTodoListName(), TodoListNameDto.class));
                }
            }
        }
        return dtos;
    }
}
