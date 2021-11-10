package com.airbus.retex.service.impl.routingComponent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.measureUnit.MeasureUnitTranslatedDto;
import com.airbus.retex.business.dto.post.PostFullDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentCreateUpdateDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFullDto;
import com.airbus.retex.business.dto.step.StepFullDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.config.RetexConfig;
import com.airbus.retex.model.IRoutingComponentModel;
import com.airbus.retex.model.inspection.Inspection;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.operation.OperationTypeBehaviorEnum;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.post.PostFieldsEnum;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.step.StepFieldsEnum;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.persistence.inspection.InspectionRepository;
import com.airbus.retex.persistence.operationType.OperationTypeRepository;
import com.airbus.retex.persistence.post.RoutingFunctionalAreaPostRepository;
import com.airbus.retex.persistence.routingComponent.RoutingComponentIndexRepository;
import com.airbus.retex.persistence.routingComponent.RoutingComponentRepository;
import com.airbus.retex.persistence.todoList.TodoListRepository;
import com.airbus.retex.service.impl.translate.TranslationMapper;
import com.airbus.retex.service.routingComponent.IRoutingComponentCreationService;
import com.airbus.retex.service.routingComponent.IRoutingComponentService;
import com.airbus.retex.service.translate.ITranslateService;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoutingComponentServiceImpl implements IRoutingComponentService {
    private static final String RETEX_ERROR_OPERATION_TYPE_NOT_FOUND = "retex.error.operation.type.not.found";
    private static final String RETEX_ERROR_ROUTING_COMPONENT_NOT_UNIQUE = "retex.error.routing.component.not.unique";
    @Autowired
    private RetexConfig retexConfig;
    @Autowired
    private RoutingComponentRepository routingComponentRepository;
    @Autowired
    private TodoListRepository todoListRepository;
    @Autowired
    private RoutingComponentIndexRepository routingComponentIndexRepository;
    @Autowired
    private OperationTypeRepository operationTypeRepository;
    @Autowired
    private IRoutingComponentCreationService routingComponentCustomService;
    @Autowired
    private InspectionRepository inspectionRepository;
    @Autowired
    private RoutingFunctionalAreaPostRepository routingFunctionalAreaPostRepository;
    @Autowired
    private DtoConverter dtoConverter;
    @Autowired
    private ITranslateService translateService;
    @Autowired
    private TranslationMapper translationMapper;
    /**
     * Returns a Routing Component or TodoList by RoutingComponentIndexId
     *
     * @param routingComponentIndexId
     * @return
     */
    @Override
    public Optional<RoutingComponentFullDto> getRoutingComponent(Long routingComponentIndexId, Long version) {
        Optional<RoutingComponentIndex> optionalRoutingComponentIndex = routingComponentIndexRepository.findByNaturalIdAndVersion(routingComponentIndexId, version);
        if(optionalRoutingComponentIndex.isPresent()){
            RoutingComponentIndex routingComponentIndex = optionalRoutingComponentIndex.get();

            if (
                    (routingComponentIndex.getRoutingComponent() == null) && (routingComponentIndex.getTodoList() == null)
            ) {
                return Optional.empty();
            }

            // if we have no routingComponent, that means we have a TodoList instead
            if (routingComponentIndex.getRoutingComponent() == null) {
                TodoList todoList = routingComponentIndex.getTodoList();
                return Optional.of(buildRoutingComponentDto(
                        routingComponentIndex,
                        todoList,
                        todoList.getInspection().getValue(),
                        todoList.getTodoListNameId(),
                        null,
                        optionalRoutingComponentIndex.get().getNaturalId()));
            } else {
                RoutingComponent routingComponent = routingComponentIndex.getRoutingComponent();
                if (routingComponentIndex.getRoutingComponent().getOperationType().isBehavior(OperationTypeBehaviorEnum.GENERIC)) {
                    return Optional.of(buildRoutingComponentDto(
                            routingComponentIndex,
                            routingComponent,
                            null,
                            null,
                            null,
                            optionalRoutingComponentIndex.get().getNaturalId()));
                }
                return Optional.of(buildRoutingComponentDto(
                        routingComponentIndex,
                        routingComponent,
                        routingComponent.getInspection().getValue(),
                        routingComponent.getFunctionality().getId(),
                        routingComponent.getDamageId(),
                        optionalRoutingComponentIndex.get().getNaturalId()
                ));
            }
        }
        return Optional.empty();
    }

    @Override
    public RoutingComponentFullDto buildRoutingComponentDto(RoutingComponentIndex routingComponentIndex, IRoutingComponentModel component, String inspectionValue, Long taskId, Long subTaskId, Long routingComponentIndexId) {

        RoutingComponentFullDto routingComponentDto = dtoConverter.toDto(component, RoutingComponentFullDto.class);
        routingComponentDto.setInspectionValue(inspectionValue);
        routingComponentDto.setTaskId(taskId);
        routingComponentDto.setCreationDate(routingComponentIndex.getCreationDate());
        routingComponentDto.setSubTaskId(subTaskId);
        routingComponentDto.setRoutingComponentIndexId(routingComponentIndex.getNaturalId());
        routingComponentDto.setVersionNumber(routingComponentIndex.getVersionNumber());
        routingComponentDto.setIsLatestVersion(routingComponentIndex.getIsLatestVersion());
        routingComponentDto.setStatus(routingComponentIndex.getStatus());


        Map<Long, Step> stepMap = component.getSteps().stream().collect(Collectors.toMap(Step::getNaturalId, step -> step));
        routingComponentDto.getSteps().forEach(stepFullDto -> {
            Step step = stepMap.get(stepFullDto.getId());
            if(null != step) {
                Map<Long, Post> postMap = step.getPosts().stream().collect(Collectors.toMap(Post::getNaturalId, post -> post));
                stepFullDto.setTranslatedFields(translationMapper.mapToTranslationFields(step.getTranslations()));

                if(routingComponentIndex.getOperationType().isBehavior(OperationTypeBehaviorEnum.VISUAL_COMPONENT)) {
                    stepFullDto.setPosts(null);
                } else {
                    stepFullDto.getPosts().forEach(postFullDto -> {
                        Post post = postMap.get(postFullDto.getId());
                        postFullDto.setTranslatedFields(translationMapper.mapToTranslationFields(post.getTranslations()));
                    });
                }


            }

        });
        return routingComponentDto;
    }

    @Override
    public List<StepFullDto> getRoutingComponentSteps(Long routingComponentIndexId) throws FunctionalException {
        Optional<RoutingComponentIndex> optionalRoutingComponentIndex = routingComponentIndexRepository.findLastVersionByNaturalId(routingComponentIndexId);
        List<StepFullDto> result = new ArrayList<>();
        if (!optionalRoutingComponentIndex.isPresent()) {
            return result;
        }
        RoutingComponentIndex routingComponentIndex = optionalRoutingComponentIndex.get();
        // if we have no routingComponent, that means we have a TodoList instead
        // and this means we have only one step (by default, if we have an empty
        // step associated to routing component if operation type behavior is different of ROUTING_COMPONENT)
        List<Step> rcSteps = new ArrayList<>();
        boolean isRoutingComponentBehavior=false;
        if(routingComponentIndex.getRoutingComponent() !=null){
            rcSteps = routingComponentIndex.getRoutingComponent().getSteps();
            isRoutingComponentBehavior = routingComponentIndex.getRoutingComponent().getOperationType().isBehavior(OperationTypeBehaviorEnum.ROUTING_COMPONENT);
        }
        if(routingComponentIndex.getTodoList() != null) {
            rcSteps.add(routingComponentIndex.getTodoList().getSteps().get(0));
            isRoutingComponentBehavior = routingComponentIndex.getTodoList().getOperationType().isBehavior(OperationTypeBehaviorEnum.ROUTING_COMPONENT);
        }

        // if behavior is todoList and we have more thant one step, or less one step, throw exception
        if (!isRoutingComponentBehavior && rcSteps.size() != 1) {
           throw new FunctionalException("retex.rc.generic.nb.steps.not.accepted");
        }

        for (Step step: rcSteps) {
            StepFullDto stepFullDto= dtoConverter.toDto(step, StepFullDto.class);
            stepFullDto.setTranslatedFields(translateService.getTranslatedFields(Step.class.getSimpleName(), step.getTechnicalId(),  EnumSet.allOf(StepFieldsEnum.class)));
            List<PostFullDto> posts=new ArrayList<>();
            for (Post post : step.getPosts()){
                PostFullDto postFullDto= dtoConverter.toDto(post, PostFullDto.class);
                postFullDto.setTranslatedFields(translateService.getTranslatedFields(Step.class.getSimpleName(), step.getTechnicalId(),  EnumSet.allOf(PostFieldsEnum.class)));
                postFullDto.setMeasureUnit( dtoConverter.toDto(post.getMeasureUnit(), MeasureUnitTranslatedDto.class));
                postFullDto.setMeasureUnitId(post.getMeasureUnitId());
                postFullDto.setNaturalId(post.getNaturalId());
                posts.add(postFullDto);
            }
            // sort list of post of the step
            posts = posts.stream().sorted(Comparator.comparingLong(PostFullDto::getId)).collect(Collectors.toList());
            stepFullDto.setPosts(posts);
            result.add(stepFullDto);
        }
        return result;
    }

    /**
     * Create RoutingComponent
     *
     * @param routingComponentCreationDto
     * @return
     */
    @Override
    public RoutingComponentFullDto createRoutingComponent(RoutingComponentCreateUpdateDto routingComponentCreationDto, Boolean published) throws FunctionalException {
        if (routingComponentCreationDto.getOperationTypeId() == null) {
            throw new FunctionalException(RETEX_ERROR_OPERATION_TYPE_NOT_FOUND);
        }
        OperationType operationType = operationTypeRepository.findById(routingComponentCreationDto.getOperationTypeId())
                .orElseThrow(() -> new FunctionalException(RETEX_ERROR_OPERATION_TYPE_NOT_FOUND));
        boolean checkTodoList = operationType.isBehavior(OperationTypeBehaviorEnum.TODO_LIST);
        boolean isInvalid = checkTodoList ? checkTodDoListUnicity(routingComponentCreationDto, 0L) : checkRoutingComponentUnicity(routingComponentCreationDto, 0L);
        if (isInvalid) {
            throw new FunctionalException(RETEX_ERROR_ROUTING_COMPONENT_NOT_UNIQUE);
        }
        return routingComponentCustomService.delegateRoutingComponentCreateOrUpdate(operationType, routingComponentCreationDto, published, null);
    }

    /**
     * Update RoutingComponent
     *
     * @param routingComponentUpdateDto
     * @return
     */
    @Override
    public RoutingComponentFullDto updateRoutingComponent(RoutingComponentCreateUpdateDto routingComponentUpdateDto, Long idIndexRoutingComponent, Boolean published) throws FunctionalException {
        RoutingComponentIndex routingComponentIndex = checkRoutingComponentType(idIndexRoutingComponent);
        IRoutingComponentModel genericRoutingComponent = routingComponentIndex.getRelatedModel();

        if (genericRoutingComponent instanceof RoutingComponent) {
            checkErrorUpdateRoutingComponent(routingComponentUpdateDto, ((RoutingComponent) genericRoutingComponent).getNaturalId());
        } else if (genericRoutingComponent instanceof TodoList) {
            checkErrorUpdateToDoList(routingComponentUpdateDto, ((TodoList) genericRoutingComponent).getNaturalId());
        }

        OperationType operationType = operationTypeRepository.findById(routingComponentUpdateDto.getOperationTypeId())
                .orElseThrow(() -> new FunctionalException(RETEX_ERROR_OPERATION_TYPE_NOT_FOUND));

        return routingComponentCustomService.delegateRoutingComponentCreateOrUpdate(operationType, routingComponentUpdateDto, published, routingComponentIndex);
    }

    /**
     * check RoutingComponent Type from IndexRoutingComponent
     *
     * @param idIndexRoutingComponent
     * @return
     */
    private RoutingComponentIndex checkRoutingComponentType(Long idIndexRoutingComponent) throws FunctionalException {
        return routingComponentIndexRepository.findLastVersionByNaturalId(idIndexRoutingComponent).orElseThrow(() -> new FunctionalException("retex.error.routing.component.index.not.found"));
    }

    private void checkErrorUpdateRoutingComponent(RoutingComponentCreateUpdateDto routingComponentUpdateDto, Long idRoutingComponent) throws FunctionalException {
        if (!routingComponentRepository.existsByNaturalIdAndOperationTypeId(idRoutingComponent, routingComponentUpdateDto.getOperationTypeId())) {
            throw new FunctionalException("retex.error.routing.component.not.found");
        }
        if (checkRoutingComponentUnicity(routingComponentUpdateDto, idRoutingComponent)) {
            throw new FunctionalException(RETEX_ERROR_ROUTING_COMPONENT_NOT_UNIQUE);
        }
    }

    private void checkErrorUpdateToDoList(RoutingComponentCreateUpdateDto routingComponentUpdateDto, Long idTodoList) throws FunctionalException {
        if (!todoListRepository.existsByNaturalIdAndOperationTypeId(idTodoList, routingComponentUpdateDto.getOperationTypeId())) {
            throw new FunctionalException("retex.error.routing.component.not.found");
        }
        if (checkTodDoListUnicity(routingComponentUpdateDto, idTodoList)) {
            throw new FunctionalException(RETEX_ERROR_ROUTING_COMPONENT_NOT_UNIQUE);
        }
    }


    private boolean checkRoutingComponentUnicity(RoutingComponentCreateUpdateDto routingComponentCreateUpdateDto, Long idRoutingComponent) throws FunctionalException {
        Inspection inspection = inspectionRepository.findByValue(routingComponentCreateUpdateDto.getInspectionValue()).orElseThrow(() -> new FunctionalException("retex.routing.component.inspection.notExists"));

        return routingComponentRepository.existsByOperationTypeIdAndInspectionIdAndFunctionalityIdAndDamageIdAndNaturalIdNot(
                routingComponentCreateUpdateDto.getOperationTypeId(),
                inspection.getId(),
                routingComponentCreateUpdateDto.getTaskId(),
                routingComponentCreateUpdateDto.getSubTaskId(),
                idRoutingComponent);
    }

    private boolean checkTodDoListUnicity(RoutingComponentCreateUpdateDto routingComponentCreateUpdateDto, Long idToDoList) throws FunctionalException {
        Inspection inspection = inspectionRepository.findByValue(routingComponentCreateUpdateDto.getInspectionValue()).orElseThrow(() -> new FunctionalException("retex.routing.component.inspection.notExists"));

        return todoListRepository.existsByOperationTypeIdAndInspectionIdAndTodoListNameIdAndNaturalIdNot(
                routingComponentCreateUpdateDto.getOperationTypeId(),
                inspection.getId(),
                routingComponentCreateUpdateDto.getTaskId(),
                idToDoList);
    }

    @Override
    public boolean isDamageLinkedToRoutingComponent(Long damageNaturalId) {
        return routingComponentRepository.existsByDamageId(damageNaturalId);
    }

}
