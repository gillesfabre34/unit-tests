package com.airbus.retex.service.impl.routing;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.damage.DamageNameDto;
import com.airbus.retex.business.dto.functionalArea.FunctionalAreaDto;
import com.airbus.retex.business.dto.functionalArea.FunctionalAreaFullDto;
import com.airbus.retex.business.dto.inspection.RoutingInspectionDetailHighlightDto;
import com.airbus.retex.business.dto.inspection.RoutingInspectionDetailsDto;
import com.airbus.retex.business.dto.operation.ListOperationDto;
import com.airbus.retex.business.dto.operation.ManageOperationDto;
import com.airbus.retex.business.dto.operation.OperationFullDto;
import com.airbus.retex.business.dto.operationPost.RoutingFunctionalAreaPostDto;
import com.airbus.retex.business.dto.post.PostCustomDto;
import com.airbus.retex.business.dto.routing.*;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentIndexNameDto;
import com.airbus.retex.business.dto.step.StepCustomDto;
import com.airbus.retex.business.dto.step.StepUpdateDto;
import com.airbus.retex.business.dto.versioning.VersionDto;
import com.airbus.retex.business.exception.ConstantExceptionRetex;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.business.mapper.CloningContext;
import com.airbus.retex.model.basic.AncestorContext;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.operation.OperationTypeBehaviorEnum;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.post.RoutingFunctionalAreaPost;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routing.RoutingFieldsEnum;
import com.airbus.retex.model.routing.RoutingTranslation;
import com.airbus.retex.model.routing.specification.RoutingSpecification;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.step.StepActivation;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import com.airbus.retex.persistence.drt.DrtRepository;
import com.airbus.retex.persistence.operationType.OperationTypeRepository;
import com.airbus.retex.persistence.part.PartRepository;
import com.airbus.retex.persistence.routing.RoutingRepository;
import com.airbus.retex.persistence.routingComponent.RoutingComponentIndexRepository;
import com.airbus.retex.persistence.todoList.TodoListRepository;
import com.airbus.retex.service.impl.operation.mapper.OperationMapper;
import com.airbus.retex.service.impl.routing.mapper.RoutingCloner;
import com.airbus.retex.service.impl.routing.mapper.RoutingMapper;
import com.airbus.retex.service.impl.todoList.mapper.TodoListMapper;
import com.airbus.retex.service.impl.translate.TranslationMapper;
import com.airbus.retex.service.impl.versionable.AbstractVersionableService;
import com.airbus.retex.service.operation.IOperationService;
import com.airbus.retex.service.routing.IRoutingService;
import com.airbus.retex.service.step.IStepActivationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoutingServiceImpl extends AbstractVersionableService<Routing, Long, RoutingCreationDto> implements IRoutingService {

    private static final String RETEX_ERROR_ROUTING_NOT_FOUND = "retex.error.routing.not.found";

    @Autowired
    private RoutingRepository routingRepository;

    @Autowired
    private OperationTypeRepository operationTypeRepository;

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private DtoConverter dtoConverter;

    @Autowired
    private IOperationService operationService;

    @Autowired
    private IStepActivationService stepActivationService;

    @Autowired
    private ChildRequestRepository childRequestRepository;

    @Autowired
    private RoutingComponentIndexRepository routingComponentIndexRepository;

    @Autowired
    private DrtRepository drtRepository;

    @Autowired
    private RoutingCloner routingCloner;

    @Autowired
    private TranslationMapper translationMapper;

    @Autowired
    private OperationMapper operationMapper;

    @Autowired
    private RoutingMapper routingMapper;

    @Autowired
    private TodoListMapper todoListMapper;

    @Autowired
    private TodoListRepository todoListRepository;

    /**
     * @param naturalId
     * @param version
     * @return
     */
    private Routing findRouting(Long naturalId, Long version) throws NotFoundException {
        return routingRepository.findByNaturalIdAndVersion(naturalId, version)
                .orElseThrow(() -> new NotFoundException(RETEX_ERROR_ROUTING_NOT_FOUND));
    }

    @Override
    public PageDto<RoutingDto> findRoutings(RoutingFilteringDto filtering) {
        Pageable pageable = PageRequest.of(filtering.getPage(), filtering.getSize(), Sort.by(Sort.Direction.DESC, "creationDate"));

        Page<Routing> routings = routingRepository.findAllLastVersions(buildSpecification(filtering), pageable);

        Map<Long, Boolean> deletableMap = new HashMap<>();
        routings.getContent()
                .forEach(routing -> deletableMap.put(routing.getTechnicalId(), isRoutingDeletable(routing)));

        List<RoutingDto> dtos = new ArrayList<>();
        routingMapper.toListDto(routings.getContent(), dtos, new AncestorContext());
        dtos.forEach(routingDto -> routingDto.setDeletable(deletableMap.get(routingDto.getTechnicalId())));

        return new PageDto<>(dtos, routings.getTotalElements(), routings.getTotalPages());
    }

    /**
     * @param filtering
     * @return
     */
    private Specification<Routing> buildSpecification(RoutingFilteringDto filtering) {
        Specification<Routing> specification = Specification.where(null);

        if (null != filtering.getName()) {
            specification = specification.and(RoutingSpecification.filterByRoutingName(filtering.getName()));
        }

        if (null != filtering.getPnOrPnRoot()) {
            specification = specification.and(RoutingSpecification.filterByPnOrPnRoot(filtering.getPnOrPnRoot()));
        }

        if (null != filtering.getNaturalId()) {
            specification = specification.and(RoutingSpecification.filterById(filtering.getNaturalId()));
        }

        if (null != filtering.getCreationDate()) {
            specification = specification.and(RoutingSpecification.filterByCreationDate(filtering.getCreationDate()));
        }

        if (null != filtering.getStatus()) {
            specification = specification.and(RoutingSpecification.filterByStatus(filtering.getStatus()));
        }

        if (null != filtering.getPartNumber()) {
            specification = specification.and(RoutingSpecification.filterByPartNumber(filtering.getPartNumber()));
        } else if (null != filtering.getPartNumberRoot()) {
            specification = specification.and(RoutingSpecification.filterByPartNumberRoot(filtering.getPartNumberRoot()));
        }
        return specification;
    }

    /**
     * @param naturalId
     * @param version
     * @return
     * @throws NotFoundException
     */
    @Override
    public RoutingFullDto findRoutingById(Long naturalId, Long version) throws NotFoundException {
        Routing routing = findRouting(naturalId, version);

        RoutingFullDto routingDto = new RoutingFullDto();
        routingMapper.toFullDto(routing, routingDto, new AncestorContext());

        return routingDto;
    }

    @Override
    public void deleteRouting(Long naturalId) throws FunctionalException {
        Routing routing = routingRepository.findLastVersionByNaturalId(naturalId)
                .orElseThrow(() -> new NotFoundException(RETEX_ERROR_ROUTING_NOT_FOUND));

        if (!isRoutingDeletable(routing)) {
            throw new FunctionalException("retex.error.routing.not.created");
        }

        deleteVersion(routing.getNaturalId());
    }

    private Boolean isRoutingDeletable(Routing routing) {
        Boolean isDeletable = Boolean.TRUE;

        if (drtRepository.countByRouting(routing) > 0) {
            isDeletable = Boolean.FALSE;
        }

        if (isDeletable && childRequestRepository.countByRoutingNaturalId(routing.getNaturalId()) > 0) {
            isDeletable = Boolean.FALSE;
        }

        return isDeletable;
    }

    @Override
    public List<OperationFullDto> manageOperation(Long idRouting, ManageOperationDto manageOperationDto) throws FunctionalException {
        List<OperationFullDto> dtoList = new ArrayList<>();

        Routing routing = updateVersion(idRouting, updateVersion -> {
            updateVersion.setStatus(EnumStatus.CREATED);
            operationService.checkOrderNumber(manageOperationDto);
            if (CollectionUtils.isNotEmpty(manageOperationDto.getOperations())) {
                operationService.updateOperations(updateVersion, manageOperationDto.getOperations());
            }

            if (CollectionUtils.isNotEmpty(manageOperationDto.getDeletedOperations())) {
                operationService.deleteOperationsByIds(updateVersion, manageOperationDto.getDeletedOperations());
            }

            if (CollectionUtils.isNotEmpty(manageOperationDto.getAddedOperations())) {
                operationService.addOperations(updateVersion, manageOperationDto.getAddedOperations());
            }
            operationService.cleanOrderNumber(updateVersion.getOperations());
        });
        operationMapper.toListFullDto(routing.getOperations(), dtoList, new AncestorContext());

        return dtoList;
    }

    @Override
    public ListOperationDto getOperationByRoutingId(Long idRouting, Long version) throws NotFoundException {
        Routing routing = findRouting(idRouting, version);

        List<FunctionalArea> functionalAreas = routing.getPart().getFunctionalAreas().stream()
                .filter(functionalArea -> !functionalArea.isDisabled()).collect(Collectors.toList());

        List<OperationFullDto> dtoListOperation = new ArrayList<>();
        operationMapper.toListFullDto(routing.getOperations(), dtoListOperation, new AncestorContext());
        ListOperationDto listOperationDto = new ListOperationDto();
        listOperationDto.setOperations(dtoListOperation);
        listOperationDto.setFunctionalAreas(dtoConverter.toDtos(functionalAreas, FunctionalAreaDto.class));

        return listOperationDto;
    }

    @Override
    public RoutingCreatedDto createRouting(RoutingCreationDto routingCreationDto, User creator) throws FunctionalException {
        boolean pnExists = routingCreationDto.getPartId() != null;
        boolean pnRootExists = !StringUtils.isEmpty(routingCreationDto.getPartNumberRoot());

        if (pnExists && pnRootExists) {
            throw new FunctionalException("retex.error.part.numbers.both.present");
        }

        // attach part
        List<Part> parts = new ArrayList<>();
        if (pnExists) {
            Optional<Part> partByPartnumberOpt = partRepository.findValidatedVersionByNaturalId(routingCreationDto.getPartId());
            if (!partByPartnumberOpt.isPresent()) {
                throw new FunctionalException("retex.error.routing.part.notExists");
            }
            parts.add(partByPartnumberOpt.get());
        } else if (pnRootExists) {
            List<Part> partByPartNumberRootList = partRepository.findPartByPartNumberRootAndStatus(routingCreationDto.getPartNumberRoot(), EnumStatus.VALIDATED);
            if (null != partByPartNumberRootList && !partByPartNumberRootList.isEmpty()) {
                parts.addAll(partByPartNumberRootList);
            } else {
                throw new FunctionalException("retex.error.routing.part.notExists");
            }
        } else {
            throw new FunctionalException("retex.part.numbers.notNull");
        }

        return addRoutings(parts, creator, routingCreationDto.getTranslatedFields());
    }

    private RoutingCreatedDto addRoutings(List<Part> partList, User creator, Map<Language, Map<RoutingFieldsEnum, String>> translatedFields) throws FunctionalException {
        List<Long> routingIds = new ArrayList<>();
        List<String> partWithRouting = new ArrayList<>();
        List<String> partWhitoutPartMapping = new ArrayList<>();

        for (Part part : partList) {

            // if we have no routing with the given part ID and part has at less one functional_area
            if (CollectionUtils.isEmpty(part.getFunctionalAreas())) {
                partWhitoutPartMapping.add(StringUtils.isEmpty(part.getPartNumber()) ? part.getPartNumberRoot() : part.getPartNumber());
            } else if (routingRepository.findByPartTechnicalIdAndIsLatestVersionTrue(part.getTechnicalId()).isEmpty()) {
                Routing routing = new Routing();
                routing.setStatus(EnumStatus.CREATED);
                routing.setCreator(creator);
                routing.setPart(part);
                translationMapper.updateEntityTranslations(routing, RoutingTranslation::new, translatedFields);

                List<OperationType> operationTypeList = operationTypeRepository.findAll();

                Integer orderNumber = 1;
                for (OperationType operationType : operationTypeList) {
                    if (operationType.isBehavior(OperationTypeBehaviorEnum.ROUTING_COMPONENT) ||
                            operationType.isBehavior(OperationTypeBehaviorEnum.VISUAL_COMPONENT)) {
                        Operation operation = new Operation();
                        operation.setOrderNumber(orderNumber);
                        operation.setOperationType(operationType);
                        routing.addOperation(operation);

                        SortedSet<FunctionalArea> functionalAreas = routing.getPart().getFunctionalAreas();
                        for (FunctionalArea functionalArea : functionalAreas) {
                            OperationFunctionalArea operationFunctionalArea = new OperationFunctionalArea();
                            operationFunctionalArea.setFunctionalArea(functionalArea);
                            operation.addOperationFunctionalArea(operationFunctionalArea);
                            stepActivationService.createAllStepActivations(operationFunctionalArea, operationType.getId());
                        }

                        orderNumber++;
                    }
                }

                createVersion(routing);
                routingIds.add(routing.getNaturalId());
            } else {
                partWithRouting.add(StringUtils.isEmpty(part.getPartNumber()) ? part.getPartNumberRoot() : part.getPartNumber());
            }
        }

        RoutingCreatedDto routingCreatedDto = new RoutingCreatedDto();

        if (!partWithRouting.isEmpty()) {
            routingCreatedDto.setErrorRoutingExists(new DefaultMessageSourceResolvable(
                    new String[]{"retex.error.routing.existing"},
                    new String[]{String.join(", ", partWithRouting)}));
        }

        if (!partWhitoutPartMapping.isEmpty()) {
            routingCreatedDto.setErrorMappingNotFound(new DefaultMessageSourceResolvable(
                    new String[]{"retex.error.routing.part.mapping.not.exists"},
                    new String[]{String.join(", ", partWhitoutPartMapping)}));
        }

        routingCreatedDto.setRoutingIds(routingIds);
        return routingCreatedDto;
    }

    @Override
    public RoutingCreatedDto duplicateRouting(RoutingCreationDto routingCreationDto, User creator, Long sourceRoutingId) throws FunctionalException {
        RoutingCreatedDto routingDuplicateDto = this.createRouting(routingCreationDto, creator);

        Optional<Routing> sourceRouting = routingRepository.findLastVersionByNaturalId(sourceRoutingId);

        if (!sourceRouting.isPresent()) {
            throw new NotFoundException(RETEX_ERROR_ROUTING_NOT_FOUND);
        }

        List<Operation> sourceOperations = new ArrayList<>();
        sourceRouting.get().getOperations().forEach(ope -> {
            if (ope.getOperationType().isBehavior(OperationTypeBehaviorEnum.TODO_LIST)) {
                sourceOperations.add(ope);
            }
        });

        for (var routingNaturalId : routingDuplicateDto.getRoutingIds()) {
            Optional<Routing> destinationRouting = routingRepository.findLastVersionByNaturalId(routingNaturalId);

            if (!destinationRouting.isPresent()) {
                throw new NotFoundException(RETEX_ERROR_ROUTING_NOT_FOUND);
            }

            copyTodoOperation(sourceOperations, destinationRouting.get());
        }

        return routingDuplicateDto;
    }

    private void copyTodoOperation(List<Operation> sourceOperations, Routing destinationRouting) throws FunctionalException {
        updateVersion(destinationRouting.getNaturalId(), updateVersion -> {
            updateVersion.setStatus(EnumStatus.CREATED);
            Integer orderNumber = destinationRouting.getOperations().size() + 1;

            //SourceOperations : les Opérations TodoList à copié
            for (Operation sourceOperation : sourceOperations) {
                Operation ope = new Operation();
                ope.setOrderNumber(orderNumber);
                ope.setOperationType(sourceOperation.getOperationType());
                destinationRouting.addOperation(ope);

                for (TodoList todoList : sourceOperation.getTodoLists()) {
                    ope.addTodoList(todoList);
                }

                orderNumber++;
            }
        });
    }

    /**
     * @param routingNaturalId routing Natural Id
     * @throws FunctionalException
     */
    @Override
    public void updateToLatestPart(Long routingNaturalId) throws FunctionalException {
        Routing routing = findRouting(routingNaturalId, null);
        Part part = partRepository.findValidatedVersionByNaturalId(routing.getPart().getNaturalId())
                .orElseThrow(() -> new NotFoundException("retex.part.notExists"));

        updateVersion(routingNaturalId, routingToUpdate -> {
            routingToUpdate.setStatus(EnumStatus.CREATED);
            routingToUpdate.setPart(part);

            Set<Operation> operations = routingToUpdate.getOperations();

            part.getFunctionalAreas().forEach(fa ->
                    operations.forEach(ope -> {

                                AtomicReference<OperationFunctionalArea> ofaRef = new AtomicReference<>();

                                ope.getOperationFunctionalAreas().forEach(ofa -> {
                                    if (ofa.getFunctionalArea().getNaturalId().equals(fa.getNaturalId())) {
                                        ofaRef.set(ofa);
                                    }
                                });

                                if (null != ofaRef.get()) {
                                    // point to new FunctionalArea version
                                    ofaRef.get().setFunctionalArea(fa);

                                    stepActivationService.createMissingStepActivations(
                                            ofaRef.get(),
                                            ope.getOperationType().getId()
                                    );
                                } else {
                                    // create new OperationFunctionalArea in version
                                    OperationFunctionalArea ofa = new OperationFunctionalArea();
                                    ofa.setFunctionalArea(fa);
                                    ope.addOperationFunctionalArea(ofa);

                                    stepActivationService.createAllStepActivations(
                                            ofa,
                                            ope.getOperationType().getId()
                                    );
                                }
                            }

                    )
            );

        });
    }

    /**
     * @param routingNaturalId               routing Natural Id
     * @param routingComponentIndexNaturalId routingComponentIndex Natural Id
     */
    @Override
    public void updateToLatestRoutingComponent(Long routingNaturalId, Long routingComponentIndexNaturalId) throws FunctionalException {
        findRouting(routingNaturalId, null);

        RoutingComponentIndex rci = routingComponentIndexRepository.findValidatedVersionByNaturalId(routingComponentIndexNaturalId)
                .orElseThrow(() -> new NotFoundException("retex.error.routing.component.index.not.found"));

        updateVersion(routingNaturalId, routingToUpdate -> {
            routingToUpdate.setStatus(EnumStatus.CREATED);
            for (Operation operation : routingToUpdate.getOperations()) {
                if (null != rci.getRoutingComponent()) { // IS ROUTINGCOMPONENT
                    operation.getOperationFunctionalAreas().forEach(operationFunctionalArea -> {
                        Long rciFunctionalityId = rci.getRoutingComponent().getFunctionality().getId();
                        if (operationFunctionalArea.getFunctionalArea().getFunctionality().getId().equals(rciFunctionalityId)
                                && operation.getOperationType().getId().equals(rci.getOperationType().getId())) {
                            List<StepActivation> newStepActivations = new ArrayList<>();

                            List<Step> newStep = rci.getRoutingComponent().getSteps();
                            newStep.forEach(step -> {
                                AtomicBoolean isPresent = new AtomicBoolean(false);
                                operationFunctionalArea.getStepActivations().forEach(stepActivation -> {
                                    if (rci.getRoutingComponent().getNaturalId().equals(stepActivation.getStep().getRoutingComponent().getNaturalId())) { // si le même routing Component
                                        if (step.getNaturalId().equals(stepActivation.getStep().getNaturalId())) {
                                            stepActivation.setStep(step);
                                            updateFunctionalAreaPostVersion(stepActivation, step.getPosts());
                                            isPresent.set(true);
                                        }
                                    }
                                });
                                if (!isPresent.get()) {
                                    StepActivation newStepActivation = new StepActivation();
                                    newStepActivation.setStep(step);
                                    newStepActivation.setActivated(Boolean.TRUE);
                                    newStepActivation.setOperationFunctionalArea(operationFunctionalArea);

                                    newStepActivations.add(newStepActivation);

                                    //Ajout d'un RoutingFunctionalAreaPost Vide pour les visual (pour la gestion du control Value)
                                    if (step.getRoutingComponent().getOperationType().isBehavior(OperationTypeBehaviorEnum.VISUAL_COMPONENT)) {
                                        RoutingFunctionalAreaPost routingFunctionalAreaPost = new RoutingFunctionalAreaPost();
                                        routingFunctionalAreaPost.setPost(step.getPosts().iterator().next());
                                        routingFunctionalAreaPost.setStepActivation(newStepActivation);
                                        routingFunctionalAreaPost.setThreshold(0F);
                                        newStepActivation.addRoutingFunctionalAreaPost(routingFunctionalAreaPost);
                                    }
                                }
                            });
                            operationFunctionalArea.getStepActivations().addAll(newStepActivations);
                        }
                    });
                } else {
                    for (TodoList todoList : operation.getTodoLists()) {
                        if (rci.getTodoList().getNaturalId().equals(todoList.getNaturalId()) && todoList.getRoutingComponentIndex().getStatus().equals(EnumStatus.OBSOLETED)) {
                            operation.removeTodoList(todoList);
                            operation.addTodoList(rci.getTodoList());
                        }
                    }
                }
            }
        });

    }

    private void updateFunctionalAreaPostVersion(StepActivation stepActivation, Set<Post> posts) {
        stepActivation.getRoutingFunctionalAreaPosts().forEach(routingFunctionalAreaPost ->
                posts.forEach(post -> {
                    if (routingFunctionalAreaPost.getPost().getNaturalId().equals(post.getNaturalId())) {
                        routingFunctionalAreaPost.setPost(post);
                    }
                })
        );
    }

    /**
     * Return header inspection information
     *
     * @param operationId
     * @param functionalAreaId
     * @return
     */
    @Override
    public FunctionalAreaFullDto getFunctionalAreaDtoByRoutingAndTaskId(
            Long routingNaturalId,
            Long operationId,
            Long functionalAreaId,
            Long version
    ) throws FunctionalException {
        Routing routing = findRouting(routingNaturalId, version);

        Operation operation = getOperationById(routing, operationId);

        if (!(operation.getOperationType().isBehavior(OperationTypeBehaviorEnum.ROUTING_COMPONENT) ||
                operation.getOperationType().isBehavior(OperationTypeBehaviorEnum.VISUAL_COMPONENT))) {
            throw new FunctionalException(ConstantExceptionRetex.OPERATION_TYPE_NOT_ALLOW);
        }

        AtomicReference<FunctionalArea> functionalAreaAtomicReference = new AtomicReference<>();
        operation.getOperationFunctionalAreas().forEach(ofa -> {
            if (ofa.getFunctionalArea().getNaturalId().equals(functionalAreaId)) {
                functionalAreaAtomicReference.set(ofa.getFunctionalArea());
            }
        });

        if (null == functionalAreaAtomicReference.get()) {
            throw new FunctionalException("retex.error.functional.area.not.found");
        }

        return dtoConverter.toDto(functionalAreaAtomicReference.get(), FunctionalAreaFullDto.class);
    }

    /**
     * Return details inspection informations
     *
     * @param routingNaturalId
     * @param operationNaturalId
     * @param taskNaturalId
     * @return
     * @throws FunctionalException
     */
    @Override
    public RoutingInspectionDetailHighlightDto getListInspectionByOperationAndTask(
            Long routingNaturalId,
            Long operationNaturalId,
            Long taskNaturalId,
            Long version
    ) throws FunctionalException { //TaskId : soit functionalAreaId soit todoListId
        Routing routing = findRouting(routingNaturalId, version);

        RoutingInspectionDetailHighlightDto routingInspectionDetailHighlights = new RoutingInspectionDetailHighlightDto();
        routingInspectionDetailHighlights.setInspectionDetail(new ArrayList<>());
        Operation operation = getOperationById(routing, operationNaturalId);

        if (operation.getOperationType().isBehavior(OperationTypeBehaviorEnum.TODO_LIST)) {
            AtomicReference<TodoList> todoListReference = new AtomicReference<>();
            operation.getTodoLists().forEach(tl -> {
                if (null != tl && tl.getNaturalId().equals(taskNaturalId)) {
                    todoListReference.set(tl);
                }
            });

            routingInspectionDetailHighlights.getInspectionDetail().add(todoListMapper.createRoutingInspectionDetailsDto(todoListReference.get()));
        } else {
            AtomicReference<OperationFunctionalArea> operationFunctionalAreaAtomicReference = new AtomicReference<>();
            operation.getOperationFunctionalAreas().forEach(ofa -> {
                if (ofa.getFunctionalArea().getNaturalId().equals(taskNaturalId)) {
                    operationFunctionalAreaAtomicReference.set(ofa);
                }
            });
            Optional<FunctionalArea> functionalAreaOptional = routing.getPart().getFunctionalAreas().stream().filter(functionalArea1 ->
                    functionalArea1.getNaturalId().equals(taskNaturalId)
            ).findFirst();

            if (functionalAreaOptional.isPresent()) {
                routingInspectionDetailHighlights = getListInspectionFunctionalArea(operationFunctionalAreaAtomicReference.get(), functionalAreaOptional.get(), operation.getOperationType());
            }

        }

        return routingInspectionDetailHighlights;
    }

    private RoutingInspectionDetailHighlightDto getListInspectionFunctionalArea(OperationFunctionalArea operationFunctionalArea, FunctionalArea functionalArea, OperationType operationType) throws FunctionalException {
        RoutingInspectionDetailHighlightDto routingInspectionDetailHighlightDto = new RoutingInspectionDetailHighlightDto();

        List<RoutingInspectionDetailsDto> routingInspectionDetailsDtoList = new ArrayList<>();
        List<RoutingComponentIndexNameDto> routingComponentIndexNameDtos = new ArrayList<>();
        for (RoutingComponent routingComponent : functionalArea.getFunctionality().getRoutingComponents()) {
            //On ne gère que les internal pour le moment
            if (!routingComponent.getInspection().getValue().equals("internal")
                    || !routingComponent.getOperationType().getId().equals(operationType.getId())) { // Que les RC correpondant à l'operationType
                continue;
            }

            List<StepCustomDto> stepCustomDtoList = new ArrayList<>();

            AtomicBoolean isPresent = new AtomicBoolean(false);
            operationFunctionalArea.getStepActivations().forEach(sa -> {
                if (sa.getStep().getRoutingComponent().getNaturalId().equals(routingComponent.getNaturalId())) {
                    isPresent.set(true);
                }
                if (sa.getStep().getRoutingComponent().getTechnicalId().equals(routingComponent.getTechnicalId())) {
                    StepCustomDto stepCustomDto = new StepCustomDto();
                    routingMapper.convertCustomStepDto(sa.getStep(), stepCustomDto);
                    stepCustomDto.setStepActivation(routingMapper.convertCustomStepActivationDto(sa));

                    //récupération des Post et threshold associé
                    List<PostCustomDto> postDtos = new ArrayList<>();
                    sa.getStep().getPosts().stream().sorted(Comparator.comparing(Post::getNaturalId))
                            .forEach(post -> {
                                PostCustomDto postCustomDto = new PostCustomDto();
                                routingMapper.convertCustomPostDto(post, postCustomDto);
                                sa.getRoutingFunctionalAreaPosts().stream().filter(routingFunctionalAreaPost ->
                                        routingFunctionalAreaPost.getStepActivation().getNaturalId().equals(sa.getNaturalId())
                                                && routingFunctionalAreaPost.getPost().getNaturalId().equals(post.getNaturalId()))
                                        .findFirst().ifPresent(routingFAPost -> postCustomDto.setPostThreshold(routingMapper.convertRoutingFAPostDto(routingFAPost)));
                                postDtos.add(postCustomDto);
                            });

                    stepCustomDto.setPosts(postDtos);
                    stepCustomDtoList.add(stepCustomDto);
                }
            });

            if (!isPresent.get() && routingComponent.getRoutingComponentIndex().getStatus().equals(EnumStatus.VALIDATED)) {  //ROUTING COMPONENT VALIDATED NON TROUVE DANS STEP ACTIVATION
                RoutingComponentIndexNameDto routingComponentIndexNameDto = new RoutingComponentIndexNameDto();
                routingComponentIndexNameDto.setIdRoutingComponentIndex(routingComponent.getRoutingComponentIndex().getNaturalId());
                routingComponentIndexNameDto.setDamage(dtoConverter.toDto(routingComponent.getDamage(), DamageNameDto.class));
                routingComponentIndexNameDtos.add(routingComponentIndexNameDto);
            }

            if (!stepCustomDtoList.isEmpty()) {
                RoutingInspectionDetailsDto routingInspectionDetailsDto = new RoutingInspectionDetailsDto();
                routingInspectionDetailsDto.setIdRoutingComponentIndex(routingComponent.getRoutingComponentIndex().getNaturalId());
                routingInspectionDetailsDto.setDamage(dtoConverter.toDto(routingComponent.getDamage(), DamageNameDto.class));
                routingInspectionDetailsDto.setIsLatestVersion(routingComponent.getRoutingComponentIndex().getIsLatestVersion());
                routingInspectionDetailsDto.setStatus(routingComponent.getRoutingComponentIndex().getStatus());
                routingInspectionDetailsDto.setSteps(stepCustomDtoList);
                routingInspectionDetailsDtoList.add(routingInspectionDetailsDto);
            }
        }
        sortInspection(routingInspectionDetailsDtoList);
        routingInspectionDetailHighlightDto.setInspectionDetail(routingInspectionDetailsDtoList);
        routingInspectionDetailHighlightDto.setNewRoutingComponentIndex(routingComponentIndexNameDtos);
        return routingInspectionDetailHighlightDto;
    }

    private void sortInspection(List<RoutingInspectionDetailsDto> routingInspectionDetailsDtoList) {
        routingInspectionDetailsDtoList.sort(Comparator.comparing(RoutingInspectionDetailsDto::getIdRoutingComponentIndex));
        routingInspectionDetailsDtoList.forEach(routingInspectionDetailsDto -> routingInspectionDetailsDto.getSteps().sort(Comparator.comparing(StepCustomDto::getStepNumber)));
    }

    @Transactional
    @Override
    public void putListInspectionDetailByPostId(Long routingNaturalId, Long operationNaturalId, Long functionalAreaNatualId, List<StepUpdateDto> stepUpdateDtoList) throws FunctionalException {
        updateVersion(routingNaturalId, routing -> {
            routing.setStatus(EnumStatus.CREATED);
            AtomicReference<OperationFunctionalArea> ofaReference = new AtomicReference<>();

            routing.getOperations().forEach(op -> {
                if (!op.getNaturalId().equals(operationNaturalId)) {
                    return;
                }

                op.getOperationFunctionalAreas().forEach(ofa -> {
                    if (ofa.getFunctionalArea().getNaturalId().equals(functionalAreaNatualId)) {
                        ofaReference.set(ofa);
                    }
                });
            });

            if (null == ofaReference.get()) {
                throw new NotFoundException("retex.operation.functionalarea.association.not.found");
            }

            OperationFunctionalArea operationFunctionalArea = ofaReference.get();

            boolean isComponent = operationFunctionalArea.getOperation().getOperationType().isBehavior(OperationTypeBehaviorEnum.ROUTING_COMPONENT) ||
                    operationFunctionalArea.getOperation().getOperationType().isBehavior(OperationTypeBehaviorEnum.VISUAL_COMPONENT);

            if (!isComponent) {
                throw new FunctionalException(ConstantExceptionRetex.OPERATION_TYPE_NOT_ALLOW);
            }

            //Les steps Activation sont initialiser à la création de la routing où à l'ajout d'opération
            Set<StepActivation> stepActivations = operationFunctionalArea.getStepActivations();
            for (StepUpdateDto stepUpdateDto : stepUpdateDtoList) {
                for (StepActivation stepActivation : stepActivations) {
                    //Si la step correspond on insert ou update le threshold
                    if (stepActivation.getStep().getNaturalId().equals(stepUpdateDto.getNaturalId())) {
                        insertOrUpdateInspectionDetail(stepActivation, stepUpdateDto.getPosts());
                        // si l'état activation a changé
                        if (stepUpdateDto.getActivated() != null && !stepActivation.getActivated().equals(stepUpdateDto.getActivated())) {
                            stepActivation.setActivated(stepUpdateDto.getActivated());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void setRoutingStatusToValidated(Long id) throws FunctionalException {
        Routing routing = routingRepository.findLastVersionByNaturalId(id).orElseThrow(() -> new NotFoundException(RETEX_ERROR_ROUTING_NOT_FOUND));
        if (routing.getStatus().equals(EnumStatus.VALIDATED)) {
            throw new FunctionalException("retex.error.routing.not.publishable");
        }

        updateVersion(id, routing1 -> routing1.setStatus(EnumStatus.VALIDATED));

    }

    private void insertOrUpdateInspectionDetail(StepActivation stepActivation, List<RoutingFunctionalAreaPostDto> postsAssociation) throws
            FunctionalException {
        List<RoutingFunctionalAreaPost> newRoutingFunctionalAreaPostList = new ArrayList<>();

        for (RoutingFunctionalAreaPostDto routingFunctionalAreaPostDto : postsAssociation) {
            //si l'assotioation est déjà créee
            boolean isAssociationCreated = false;
            AtomicReference<Post> postReference = new AtomicReference<>();
            stepActivation.getStep().getPosts().forEach(p -> {
                if (p.getNaturalId().equals(routingFunctionalAreaPostDto.getPostId())) {
                    postReference.set(p);
                }
            });

            if (null == postReference.get()) {
                throw new NotFoundException("retex.post.not.found");
            }

            if (null == routingFunctionalAreaPostDto.getThreshold()) { // si threshold != null
                continue;
            }

            for (RoutingFunctionalAreaPost routingFunctionalAreaPost : stepActivation.getRoutingFunctionalAreaPosts()) {
                if (routingFunctionalAreaPost.getPost().getNaturalId().equals(routingFunctionalAreaPostDto.getPostId())) {
                    routingFunctionalAreaPost.setThreshold(routingFunctionalAreaPostDto.getThreshold());
                    //Update de l'opération post
                    isAssociationCreated = true;
                }
            }

            // si l'association n'existe pas on insert
            if (!isAssociationCreated) {
                RoutingFunctionalAreaPost routingFunctionalAreaPostToInsert = new RoutingFunctionalAreaPost();
                routingFunctionalAreaPostToInsert.setThreshold(routingFunctionalAreaPostDto.getThreshold());
                routingFunctionalAreaPostToInsert.setPost(postReference.get());
                routingFunctionalAreaPostToInsert.setStepActivation(stepActivation);
                newRoutingFunctionalAreaPostList.add(routingFunctionalAreaPostToInsert);
            }
        }

        stepActivation.getRoutingFunctionalAreaPosts().addAll(newRoutingFunctionalAreaPostList);
    }

    @Override
    protected void mapDtoToVersion(RoutingCreationDto updateDto, Routing version) throws FunctionalException {
//do nothing
    }

    @Override
    protected Routing cloneVersion(Routing version) {
        return routingCloner.cloneRouting(version, new CloningContext());
    }

    @Override
    public List<VersionDto> findAllVersionsByNaturalId(Long naturalId) {
        List<Routing> routingList = routingRepository.findAllVersionsByNaturalId(naturalId);
        return dtoConverter.toDtos(routingList, VersionDto.class);
    }

    /**
     * @param routing
     * @param operationId
     * @return
     */
    private Operation getOperationById(Routing routing, Long operationId) throws NotFoundException {
        AtomicReference<Operation> operationReference = new AtomicReference<>();

        routing.getOperations().forEach(ope -> {
            if (ope.getNaturalId().equals(operationId)) {
                operationReference.set(ope);
            }
        });

        if (null == operationReference.get()) {
            throw new NotFoundException("retex.error.operation.not.found");
        }

        return operationReference.get();
    }
}
