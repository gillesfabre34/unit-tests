package com.airbus.retex.service.impl.routingComponent;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.inspection.InspectionDto;
import com.airbus.retex.business.dto.operationType.OperationTypeDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFilterDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFilterRequestDto;
import com.airbus.retex.business.dto.subTask.SubTaskDamageDto;
import com.airbus.retex.business.dto.task.TaskDto;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.damage.Damage;
import com.airbus.retex.model.functionality.Functionality;
import com.airbus.retex.model.functionality.specification.FunctionalitySpecification;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.operation.OperationTypeBehaviorEnum;
import com.airbus.retex.model.task.TodoListName;
import com.airbus.retex.model.todoList.specification.TodoListSpecification;
import com.airbus.retex.persistence.damage.DamageRepository;
import com.airbus.retex.persistence.functionality.FunctionalityRepository;
import com.airbus.retex.persistence.inspection.InspectionRepository;
import com.airbus.retex.persistence.operationType.OperationTypeRepository;
import com.airbus.retex.persistence.todoList.TodoListNameRepository;
import com.airbus.retex.service.routingComponent.IRoutingComponentFiltersService;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoutingComponentFiltersServiceImpl implements IRoutingComponentFiltersService {

    @Autowired
    private DtoConverter dtoConverter;
    @Autowired
    private OperationTypeRepository operationTypeRepository;
    @Autowired
    private FunctionalityRepository functionalityRepository;
    @Autowired
    private TodoListNameRepository todoListNameRepository;
    @Autowired
    private DamageRepository damageRepository;
    @Autowired
    private InspectionRepository inspectionRepository;


    @Override
    public RoutingComponentFilterDto getRoutingComponentFilters(RoutingComponentFilterRequestDto filters) {
        RoutingComponentFilterDto routingComponentFilterDto = new RoutingComponentFilterDto();

        Optional<OperationType> operationTypeOpt = null != filters.getOperationTypeId() ? operationTypeRepository.findById(filters.getOperationTypeId()) : Optional.empty();

        List<OperationTypeDto> operationTypeDtoList = dtoConverter.convert(operationTypeRepository.findAll(), OperationTypeDto::new);
        routingComponentFilterDto.setOperationTypeList(operationTypeDtoList);

        initTaskList(filters, routingComponentFilterDto, operationTypeOpt);

        initSubtaskList(filters, routingComponentFilterDto, operationTypeOpt);

        // Put only internal in inspectionDtoList if TaskList is empty
        List<InspectionDto> inspectionDtoList;
        if (routingComponentFilterDto.getTodoListList() == null || routingComponentFilterDto.getTodoListList().isEmpty()) {
            inspectionDtoList = dtoConverter.convert(inspectionRepository.findByValue("internal")
                    .stream().collect(Collectors.toList()), InspectionDto::new);
        } else {
            inspectionDtoList = dtoConverter.convert(inspectionRepository.findAll(), InspectionDto::new);
        }
        routingComponentFilterDto.setInspectionList(inspectionDtoList);

        List<EnumStatus> statusList = new ArrayList<>(EnumSet.of(EnumStatus.CREATED,EnumStatus.VALIDATED));
        routingComponentFilterDto.setStatusList(statusList);

        return routingComponentFilterDto;
    }


    private void initTaskList(RoutingComponentFilterRequestDto filters, RoutingComponentFilterDto routingComponentFilterDto, Optional<OperationType> operationTypeOpt) {


        // Si pas d'operationType on recherche les todoListName et les functionality
        if (!operationTypeOpt.isPresent() || (
                (operationTypeOpt.get().isBehavior(OperationTypeBehaviorEnum.ROUTING_COMPONENT) ||
                        operationTypeOpt.get().isBehavior(OperationTypeBehaviorEnum.VISUAL_COMPONENT)))) {
            Specification<Functionality> functionalitySpec = buildSpecificationRoutingComponent(filters);

            List<Functionality> functionalityList = functionalityRepository.findAll(functionalitySpec);
            List<TaskDto> functionalityTaskDtoList = dtoConverter.convert(functionalityList, () -> new TaskDto(Functionality.class));
            routingComponentFilterDto.setFunctionalityList(functionalityTaskDtoList);
        }

        if (!operationTypeOpt.isPresent() || (operationTypeOpt.get().isBehavior(OperationTypeBehaviorEnum.TODO_LIST))) {
            // s'il y a une SubTask on ne recherche pas les todoListName
            if (null == filters.getSubTaskId()) {
                Specification<TodoListName> todoListSpec = buildSpecificationTodoList(filters, operationTypeOpt);

                List<TodoListName> todoListList = todoListNameRepository.findAll(todoListSpec);
                List<TaskDto> todoListTaskDtoList = dtoConverter.convert(todoListList, () -> new TaskDto(TodoListName.class));
                routingComponentFilterDto.setTodoListList(todoListTaskDtoList);
            }
        }
    }


    private void initSubtaskList(RoutingComponentFilterRequestDto filters, RoutingComponentFilterDto routingComponentFilterDto, Optional<OperationType> operationTypeOpt) {

        boolean isTodoListOperationType = false;

        if (operationTypeOpt.isPresent()) {
            isTodoListOperationType = operationTypeOpt.get().isBehavior(OperationTypeBehaviorEnum.TODO_LIST);

        }

        List<Damage> damageList;
        if ((isTodoListOperationType && null != filters.getOperationTypeId()) || null != filters.getTodoListNameId()) {
            damageList = new ArrayList<>();
        } else {
            if (null != filters.getFunctionalityId()) {
                damageList = damageRepository.findByFunctionalityIdAndStateActive(filters.getFunctionalityId());
            } else {
                damageList = damageRepository.findAllValidatedVersions();
            }
        }

        List<SubTaskDamageDto> subTaskDamageDtoList = dtoConverter.toDtos(damageList, SubTaskDamageDto.class);
        routingComponentFilterDto.setSubTaskList(subTaskDamageDtoList);
    }

    /**
     * @param filters
     * @return
     */
    private Specification<TodoListName> buildSpecificationTodoList(RoutingComponentFilterRequestDto filters, Optional<OperationType> operationTypeOpt) {
        Specification<TodoListName> todoListSpec = Specification.where(null);

        boolean isTodoListOperationType = false;

        // si To_do list
        if (operationTypeOpt.isPresent()) {
            isTodoListOperationType = operationTypeOpt.get().isBehavior(OperationTypeBehaviorEnum.TODO_LIST);
        }

        // si on a un opérationID
        if (null != filters.getOperationTypeId() && isTodoListOperationType) {
            todoListSpec = Specification.where(TodoListSpecification.filterByOperationId(filters.getOperationTypeId()));
        }

        return todoListSpec;
    }


    /**
     * @param filters
     * @return
     */
    private Specification<Functionality> buildSpecificationRoutingComponent(RoutingComponentFilterRequestDto filters) {
        Specification<Functionality> functionalitySpec = Specification.where(null);
        if (null != filters.getSubTaskId()) {
            functionalitySpec = Specification.where(FunctionalitySpecification.filterByDamageId(filters.getSubTaskId()));
        }
        return functionalitySpec;
    }

}



