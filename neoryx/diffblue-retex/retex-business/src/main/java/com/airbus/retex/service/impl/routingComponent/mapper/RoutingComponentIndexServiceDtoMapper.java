package com.airbus.retex.service.impl.routingComponent.mapper;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentIndexDto;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.persistence.Step.StepActivationRepository;
import com.airbus.retex.persistence.todoList.TodoListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoutingComponentIndexServiceDtoMapper {

    @Autowired
    private DtoConverter dtoConverter;
    @Autowired
    private StepActivationRepository stepActivationRepository;
    @Autowired
    private TodoListRepository todoListRepository;

    public List<RoutingComponentIndexDto> pageRoutingComponentIndexToDto(Page<RoutingComponentIndex> pageRoutingComponentIndex) {
        List<RoutingComponentIndexDto> routingComponentIndexDtos = new ArrayList<>();
        pageRoutingComponentIndex.stream().forEach(routingComponentIndex -> {
            RoutingComponentIndexDto routingComponentIndexDto = dtoConverter.toDto(routingComponentIndex, RoutingComponentIndexDto.class);
            if (routingComponentIndex.getRoutingComponent() != null) {
                List<Long> stepIds = routingComponentIndex.getRoutingComponent().getSteps().stream()
                        .map(Step::getTechnicalId).collect(Collectors.toList());
                routingComponentIndexDto.setDeletable(!stepActivationRepository.existsByStepTechnicalIdIn(stepIds));
            }
            if (routingComponentIndex.getTodoList() != null) {
                TodoList todoList = routingComponentIndex.getTodoList();
                routingComponentIndexDto.setDeletable(0 == todoListRepository.countRelatedOperations(todoList.getTechnicalId()));
            }
            routingComponentIndexDtos.add(routingComponentIndexDto);
        });
        return routingComponentIndexDtos;
    }
}