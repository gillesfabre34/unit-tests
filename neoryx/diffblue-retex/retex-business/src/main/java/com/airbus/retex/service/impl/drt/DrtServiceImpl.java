package com.airbus.retex.service.impl.drt;

import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.drt.DrtDto;
import com.airbus.retex.business.dto.drt.DrtFilteringDto;
import com.airbus.retex.business.dto.drt.DrtHeaderDto;
import com.airbus.retex.business.dto.functionalArea.FunctionalAreaHeaderDto;
import com.airbus.retex.business.dto.inspection.InspectionDetailsDto;
import com.airbus.retex.business.dto.inspection.InspectionValueDto;
import com.airbus.retex.business.dto.operation.ListDrtOperationDto;
import com.airbus.retex.business.dto.operation.OperationFunctionalAreaStatusDto;
import com.airbus.retex.business.exception.ConstantExceptionRetex;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.business.exception.TechnicalError;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.common.StepType;
import com.airbus.retex.model.control.ControlTodoList;
import com.airbus.retex.model.drt.AbstractDrtOperationStatus;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.drt.DrtOperationStatusFunctionalArea;
import com.airbus.retex.model.drt.DrtOperationStatusTodoList;
import com.airbus.retex.model.drt.specification.DrtSpecification;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.operation.OperationTypeBehaviorEnum;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.step.StepActivation;
import com.airbus.retex.persistence.Step.StepActivationRepository;
import com.airbus.retex.persistence.admin.UserRepository;
import com.airbus.retex.persistence.drt.DrtPicturesRepository;
import com.airbus.retex.persistence.drt.DrtRepository;
import com.airbus.retex.persistence.operation.OperationFunctionalAreaRepository;
import com.airbus.retex.service.drt.IDrtService;
import com.airbus.retex.service.impl.admin.UserServiceImpl;
import com.airbus.retex.service.impl.drt.mapper.DrtDtoMapper;
import com.airbus.retex.service.impl.todoList.mapper.TodoListMapper;
import com.airbus.retex.service.impl.util.RetexStreamUtil;
import com.airbus.retex.service.routing.IRoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
@Transactional(rollbackFor = Exception.class)
public class DrtServiceImpl implements IDrtService {

    @Autowired
    private IRoutingService routingService;

    @Autowired
    private DrtRepository drtRepository;

    @Autowired
    private DrtDtoMapper drtDtoMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OperationFunctionalAreaRepository operationFunctionalAreaRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private TodoListMapper todoListMapper;

    @Autowired
    private DrtPicturesRepository drtPicturesRepository;

    @Autowired
    private StepActivationRepository stepActivationRepository;

    /**
     * @param userId
     * @param drtFilteringDto
     * @return PageDto<DrtDto>
     */
    @Override
    public PageDto<DrtDto> findDrtsWithFilteringAndUserRoles(final Long userId, final DrtFilteringDto drtFilteringDto) {

        Pageable pageable = PageRequest.of(drtFilteringDto.getPage(), drtFilteringDto.getSize(), Sort.by("integrationDate"));
        Specification<Drt> specificationDrt = buildSpecification(drtFilteringDto, userId);

        if (specificationDrt != null) {
            Page<Drt> drts = drtRepository.findAll(specificationDrt, pageable);
            return new PageDto<>(drtDtoMapper.convert(drts.getContent()), drts.getTotalElements(), drts.getTotalPages());
        } else {
            return new PageDto<>(new ArrayList<>(), 0L, 0);
        }

    }

    /**
     * @param drtId drt technical Id
     * @return Drt
     * @throws NotFoundException if drt not exist
     */
    @Override
    public Drt findDrtById(Long drtId) throws NotFoundException {
        return drtRepository.findById(drtId).orElseThrow(() -> new NotFoundException("retex.error.drt.not.found"));
    }

    /**
     * @param drtId    drt technical Id
     * @param language
     * @return DrtHeaderDto
     * @throws FunctionalException
     */
    @Override
    public DrtHeaderDto getHeader(Long drtId, Language language) throws FunctionalException {
        Drt drt = findDrtById(drtId);
        return drtDtoMapper.convertToHeader(drt, language);
    }

    @Override
    public FunctionalAreaHeaderDto getOperationTaskHeader(Long drtId, Long operationId, Long taskId) throws FunctionalException {
        Routing routing = getRoutingByDrt(drtId);
        List<Operation> operations = routing.getOperations().stream()
                .filter(operation -> operation.getNaturalId().equals(operationId)).collect(Collectors.toList());

        if (operations.size() != 1) {
            throw new FunctionalException("retex.error.drt.operation.found.not.unique");
        }
        Operation operation = operations.get(0);

        if (operation.getOperationType().isBehavior(OperationTypeBehaviorEnum.ROUTING_COMPONENT)
                || operation.getOperationType().isBehavior(OperationTypeBehaviorEnum.VISUAL_COMPONENT)) {
            List<OperationFunctionalArea> operationFAList = operation.getOperationFunctionalAreas().stream()
                    .filter(opFA -> opFA.getFunctionalArea().getNaturalId().equals(taskId))
                    .collect(Collectors.toList());

            if (operationFAList.size() != 1) {
                throw new FunctionalException("retex.error.drt.operation.functional.area.found.not.unique");
            }
            return drtDtoMapper.convertOperationTaskToHeader(operationFAList.get(0).getFunctionalArea());
        } else {
            throw new FunctionalException(ConstantExceptionRetex.OPERATION_TYPE_NOT_ALLOW);
        }
    }

    private Specification<Drt> buildSpecification(final DrtFilteringDto drtFilteringDto, final Long userId) {
        Specification<Drt> specification = Specification.where(null);

        if (null != drtFilteringDto.getSerialNumber()) {
            specification = specification.and(DrtSpecification.filterBySerialNumber(drtFilteringDto.getSerialNumber()));
        }

        if (null != drtFilteringDto.getPartNumber()) {

            specification = specification.and(DrtSpecification.filterByPartNumber(drtFilteringDto.getPartNumber()));
        }

        if (userService.isAuthentifiedUserHasTechnicalResponsibleRole(userId)) {  //- A technical responsible sees DRTs that come from requests where he is assigned
            specification = specification.and(DrtSpecification.filterByTechnicalResponsible(userId));
        } else if (userService.isAuthentifiedUserHasOperatorRole(userId)) {  //- An operator sees DRTs unassigned or assigned to him if those DRT come from requests where he is assigned
            specification = specification.and(DrtSpecification.filterByOperator(userId));
        }

        return specification;
    }

    /**
     * @param drtId drt technical Id
     * @return ListOperationDto
     * @throws FunctionalException
     */
    @Override
    public ListDrtOperationDto getOperationsByDrtId(Long drtId) throws FunctionalException {
        Drt drt = findDrtById(drtId);
        Routing routing = getRoutingByDrt(drtId);
        ListDrtOperationDto listDrtOperationDto = new ListDrtOperationDto(routingService.getOperationByRoutingId(routing.getNaturalId(), routing.getVersionNumber()));

        if (null != listDrtOperationDto.getOperations()) {
            listDrtOperationDto.getOperations().forEach(operationFullDto -> {
                if (operationFullDto.getOperationType().getBehavior().equals(OperationTypeBehaviorEnum.TODO_LIST)) {
                    if (null != operationFullDto.getOperationTodoLists()) {
                        operationFullDto.getOperationTodoLists()
                                .forEach(operationTodoListDto -> operationTodoListDto
                                        .setOperationStatus(getOperationStatusTodoList(drt, operationFullDto.getId(),
                                                operationTodoListDto.getTodoList().getId())));
                    }
                } else if (operationFullDto.getOperationType().getBehavior().equals(OperationTypeBehaviorEnum.ROUTING_COMPONENT)
                        || operationFullDto.getOperationType().getBehavior().equals(OperationTypeBehaviorEnum.VISUAL_COMPONENT)) {
                    List<OperationFunctionalAreaStatusDto> operationFunctionalAreaStatusDtos = new ArrayList<>();
                    if (null != listDrtOperationDto.getFunctionalAreas()) {
                        listDrtOperationDto.getFunctionalAreas().forEach(functionalAreaDto -> {
                            OperationFunctionalAreaStatusDto operationFunctionalAreaStatusDto = new OperationFunctionalAreaStatusDto();
                            operationFunctionalAreaStatusDto.setId(functionalAreaDto.getId());
                            operationFunctionalAreaStatusDto.setStatus(getOperationStatusRC(drt, operationFullDto.getId(), functionalAreaDto.getId()));
                            operationFunctionalAreaStatusDtos.add(operationFunctionalAreaStatusDto);
                        });
                        operationFunctionalAreaStatusDtos.stream()
                                .sorted(Comparator.comparing(OperationFunctionalAreaStatusDto::getId))
                                .collect(Collectors.toList());
                    }
                    operationFullDto.setFunctionalAreaStatus(operationFunctionalAreaStatusDtos);
                }
            });
        }
        listDrtOperationDto.setClosable(isClosable(drt));
        return listDrtOperationDto;
    }


    /**
     * Return the routing by Drt according to drt status
     *
     * @param drtId drt technical Id
     * @return Routing
     * @throws FunctionalException
     */
    private Routing getRoutingByDrt(Long drtId) throws FunctionalException {
        Drt drt = findDrtById(drtId);
        Routing routing = null;
        if (drt.getStatus().equals(EnumStatus.IN_PROGRESS)) {
            routing = drt.getRouting();
        } else if (null != drt.getChildRequest()) {
            routing = drt.getChildRequest().getRouting();
        }

        if (null == routing) {
            throw new FunctionalException("retex.error.drt.routing.not.found");
        }
        return routing;
    }

    /**
     * Return Operation By Drt And Operation
     *
     * @param drtId       drt technical Id
     * @param operationId operation Natural Id
     * @return Operation
     * @throws FunctionalException
     */
    public Operation getOperationByDrtAndOperation(Long drtId, Long operationId) throws FunctionalException {
        Routing routing = getRoutingByDrt(drtId);
        List<Operation> operations = routing.getOperations().stream()
                .filter(operation -> operation.getNaturalId().equals(operationId)).collect(Collectors.toList());

        if (operations.size() > 1) {
            throw new FunctionalException("retex.error.drt.operation.found.not.unique");
        }
        if (operations.isEmpty()) {
            throw new FunctionalException("retex.error.drt.operation.not.found");
        }

        return operations.get(0);
    }

    /**
     * Returns inspection details of each "routing component" for an operation, by operation type
     *
     * @param drtId       drt technical Id
     * @param operationId operation Natural Id
     * @param taskId      functionalArea Natural Id if RoutingComponent / todolistNaturalId if TodoList
     * @return List<InspectionDetailsDto>
     * @throws FunctionalException
     */
    @Override
    public List<InspectionDetailsDto> getInspectionDetails(Long drtId, Long operationId, Long taskId) throws FunctionalException {
        List<InspectionDetailsDto> inspectionDetailsDtos = new ArrayList<>();
        Drt drt = findDrtById(drtId);
        Operation operation = getOperationByDrtAndOperation(drtId, operationId);

        if (operation.getOperationType().isBehavior(OperationTypeBehaviorEnum.ROUTING_COMPONENT) ||
                operation.getOperationType().isBehavior(OperationTypeBehaviorEnum.VISUAL_COMPONENT)) {
            inspectionDetailsDtos = getRoutingComponentInspectionDetail(drt, operation, taskId);

        } else if (operation.getOperationType().isBehavior(OperationTypeBehaviorEnum.TODO_LIST)) {
            inspectionDetailsDtos.add(getToDoListInspectionDetail(drt, operation, taskId));

        } else {
            throw new FunctionalException(ConstantExceptionRetex.OPERATION_TYPE_NOT_ALLOW);
        }
        return inspectionDetailsDtos;
    }

    public OperationFunctionalArea getOperationFunctionalArea(Operation operation, Long taskId) throws FunctionalException {
        List<OperationFunctionalArea> operationFAList = operation.getOperationFunctionalAreas().stream()
                .filter(opFA -> opFA.getFunctionalArea().getNaturalId().equals(taskId))
                .collect(Collectors.toList());

        if (operationFAList.size() != 1) {
            throw new FunctionalException("retex.error.drt.operation.functional.area.found.not.unique");
        }
        return operationFAList.get(0);
    }


    /**
     * For each routing components, returns all details mapped by corresponding drt, operation and functionality
     * Available for ROUTING_COMPONENT and VISUAL_COMPPONENT behaviors
     *
     * @param drt
     * @param operation natural Id
     * @param taskId natural Id
     * @return List<InspectionDetailsDto>
     * @throws FunctionalException
     */
    private List<InspectionDetailsDto> getRoutingComponentInspectionDetail(Drt drt, Operation operation, Long taskId) throws FunctionalException {
        OperationFunctionalArea operationFunctionalArea = getOperationFunctionalArea(operation, taskId);

        EnumStatus statusOperation = getOperationStatusRC(drt, operation.getNaturalId(), taskId);

        // Get steps activation which have a threshold
        List<StepActivation> stepActivations = operationFunctionalArea.getStepActivations().stream()
                .filter(stepActivation -> !stepActivation.getRoutingFunctionalAreaPosts().isEmpty())
                .collect(Collectors.toList());

        // Get routing components of steps activation filtered
        List<RoutingComponent> routingComponents = stepActivations.stream()
                .map(stepActivation -> stepActivation.getStep().getRoutingComponent()).distinct()
                .filter(rc -> rc.getOperationType().getId().equals(operation.getOperationType().getId()))
                .sorted(Comparator.comparingLong(RoutingComponent::getNaturalId))
                .collect(Collectors.toList());
        List<InspectionDetailsDto> inspectionDetailsDtos = new ArrayList<>();

        for (RoutingComponent routingComponent : routingComponents) {
            List<StepActivation> stepActivationsByRoutingComponent = stepActivations.stream()
                    .filter(stepActivation -> stepActivation.getActivated() &&
                            stepActivation.getStep().getRoutingComponent().getNaturalId().equals(routingComponent.getNaturalId()))
                    .sorted(Comparator.comparing((StepActivation stepActivation) -> stepActivation.getStep().compareTo(StepType.AUTO))
                            .thenComparing(stepActivation -> stepActivation.getStep().getNaturalId()))
                    .collect(Collectors.toList());
            if (!stepActivationsByRoutingComponent.isEmpty()) {
                InspectionDetailsDto inspectionDetailsDto = drtDtoMapper.convertInspectionDetails(routingComponent);
                inspectionDetailsDto.setSteps(drtDtoMapper.convertListCustomStepDto(stepActivationsByRoutingComponent, drt));
                inspectionDetailsDto.setStatusOperation(statusOperation);

                drt.getQCheckRoutingComponents().stream().filter(qcheckRoutingComponent -> qcheckRoutingComponent.getRoutingComponentIndex().getNaturalId().equals(routingComponent.getRoutingComponentIndex().getNaturalId())
                    && qcheckRoutingComponent.getOperationFunctionalArea().getOperation().getNaturalId().equals(operation.getNaturalId())
                    && qcheckRoutingComponent.getOperationFunctionalArea().getFunctionalArea().getNaturalId().equals(taskId))
                    .collect(RetexStreamUtil.findOneOrEmpty())
                    .ifPresent(qcheckRoutingComponent -> inspectionDetailsDto.setQcheckValue(qcheckRoutingComponent.getValue()));
                inspectionDetailsDtos.add(inspectionDetailsDto);
            }
        }
        return inspectionDetailsDtos;
    }

    private EnumStatus getOperationStatusRC(Drt drt, Long operationId, Long taskId) {
        Optional<DrtOperationStatusFunctionalArea> operationStatus = drt.getOperationStatus().stream().filter(abstractDrtOperationStatus -> abstractDrtOperationStatus instanceof DrtOperationStatusFunctionalArea)
                .map(abstractDrtOperationStatus -> (DrtOperationStatusFunctionalArea) abstractDrtOperationStatus)
                .filter(drtOperationStatusFunctionalArea -> drtOperationStatusFunctionalArea.getOperationFunctionalArea().getFunctionalArea().getNaturalId().equals(taskId))
                .filter(drtOperationStatusFunctionalArea -> drtOperationStatusFunctionalArea.getOperationFunctionalArea().getOperation().getNaturalId().equals(operationId)).findFirst();

        if (operationStatus.isPresent()) {
            return operationStatus.get().getStatus();
        } else {
            return EnumStatus.CREATED;
        }
    }

    private EnumStatus getOperationStatusTodoList(Drt drt, Long operationId, Long taskId) {
        Optional<DrtOperationStatusTodoList> operationStatus = drt.getOperationStatus().stream().filter(abstractDrtOperationStatus -> abstractDrtOperationStatus instanceof DrtOperationStatusTodoList)
                .map(abstractDrtOperationStatus -> (DrtOperationStatusTodoList) abstractDrtOperationStatus)
                .filter(drtOperationStatusTodoList -> drtOperationStatusTodoList.getOperation().getNaturalId().equals(operationId))
                .filter(drtOperationStatusTodoList -> drtOperationStatusTodoList.getTodoList().getNaturalId().equals(taskId)).findFirst();
        if (operationStatus.isPresent()) {
            return operationStatus.get().getStatus();
        } else {
            return EnumStatus.CREATED;
        }
    }

    /**
     * For a todolist, returns all details mapped by corresponding drt, operation and todolist id
     * Available for TODOLIST behavior
     *
     * @param drt
     * @param operation natural Id
     * @param taskId    natural Id
     * @return InspectionDetailsDto
     * @throws FunctionalException
     */
    private InspectionDetailsDto getToDoListInspectionDetail(Drt drt, Operation operation, Long taskId) throws FunctionalException {
        InspectionDetailsDto inspectionDetailsDto = new InspectionDetailsDto();
        List<ControlTodoList> controlTodoLists = getControlsTodoList(drt, operation, taskId);

        if (controlTodoLists.size() == 1) {
            inspectionDetailsDto = todoListMapper.createInspectionDetailsDto(controlTodoLists.get(0).getTodoList());
            inspectionDetailsDto.setTodoListValue(controlTodoLists.get(0).getValue());
        }

        if (controlTodoLists.isEmpty()) { // si pas de valeur initialisé
            inspectionDetailsDto = todoListMapper.createInspectionDetailsDto(operation, taskId);
        }
        inspectionDetailsDto.setStatusOperation(getOperationStatusTodoList(drt, operation.getNaturalId(), taskId));

        return inspectionDetailsDto;
    }

    /**
     * @param drt
     * @param operation natural Id
     * @param taskId    natural Id
     * @return
     * @throws FunctionalException
     */
    private List<ControlTodoList> getControlsTodoList(Drt drt, Operation operation, Long taskId) throws FunctionalException {
        List<ControlTodoList> controlTodoLists = drt.getControls().stream()
                .filter(abstractControl -> abstractControl instanceof ControlTodoList)
                .map(abstractControl -> (ControlTodoList) abstractControl)
                .filter(controlTodoList -> controlTodoList.getOperation().getNaturalId().equals(operation.getNaturalId())
                        && controlTodoList.getTodoList().getNaturalId().equals(taskId))
                .collect(Collectors.toList());

        if (controlTodoLists.size() > 1) {
            throw new FunctionalException("retex.error.todolist.not.unique");
        }
        return controlTodoLists;
    }

    /**
     * USER should be the operator assign to the drt or a tech user (for Qcheck)
     * If drt is Created, the drt status change to IN_PROGRESS and the Routing of the drt is fixed
     * if all Operation are VALIDATED the drt status change TO VALIDATED
     * A VALIDATED operation can't be updated
     *
     * @param drtId              drt technical Id
     * @param operationId        operation Natural Id
     * @param taskId             functionalArea Natural Id if RoutingComponent / todolistNaturalId if TodoList
     * @param inspectionValueDto inspectionValueDto
     * @throws FunctionalException
     */
    @Override
    public void putInspectionDetails(Long drtId, Long operationId, Long taskId, InspectionValueDto inspectionValueDto, Long userId, boolean validate) throws FunctionalException {
        Drt drt = findDrtById(drtId);
        checkUser(drt, userId);
        checkDrtStatusInProgress(drt);
        drtDtoMapper.updateDrt(drt, getOperationByDrtAndOperation(drtId, operationId), taskId, inspectionValueDto, validate);

        // Validate Drt if isValidatable
        if (validate) {
            validateDrt(drt);
        }

        drtRepository.save(drt);
    }

    private void checkUser(Drt drt, Long userId) throws FunctionalException {
        if (userService.isAuthentifiedUserHasAdminRole(userId)) {
            return;
        }
        if (userService.isAuthentifiedUserHasOperatorRole(userId)) {
            if (drt.getStatus().equals(EnumStatus.CREATED) || drt.getAssignedOperator() == null) {
                drt.setAssignedOperator(userRepository.getById(userId));
            } else if (!drt.getAssignedOperator().getId().equals(userId)) {
                throw new FunctionalException("retex.error.drt.not.assign");
            }
        } else {
            throw new FunctionalException("retex.error.drt.wrong.role");
        }
    }

    private void checkDrtStatusInProgress(Drt drt) throws FunctionalException {
        if (drt.getStatus().equals(EnumStatus.CREATED)) {
            drt.setStatus(EnumStatus.IN_PROGRESS);
            drt.setRouting(drt.getChildRequest().getRouting());
        } else if (!drt.getStatus().equals(EnumStatus.IN_PROGRESS)) {
            throw new FunctionalException("retex.error.drt.wrong.status.update");
        }
    }

    /**
     * Return true if drt can be set to targetStatus.
     *
     * @param drt
     * @throws TechnicalError
     */
    @Override
    public boolean isValidatable(Drt drt) {
        if (null == drt.getRouting() || drt.getOperationStatus().isEmpty()) {
            return false;
        }

        AtomicInteger totalOperationStatuses = new AtomicInteger();
        drt.getRouting().getOperations().forEach(ope -> {
            totalOperationStatuses.addAndGet(ope.getTodoLists().size());
        });
        totalOperationStatuses.addAndGet(operationFunctionalAreaRepository.countAllOperationFunctionalAreasActivated(drt.getRouting()));

        if (totalOperationStatuses.get() != drt.getOperationStatus().size()) {
            return false;
        }

        boolean updateStatus = true;
        List<AbstractDrtOperationStatus> drtOperationStatuses = new ArrayList<>(drt.getOperationStatus());

        int i = 0;
        while (i < drtOperationStatuses.size() && updateStatus) {
            updateStatus = drtOperationStatuses.get(i).getStatus().equals(EnumStatus.VALIDATED);
            i++;
        }

        return updateStatus;
    }

    /**
     * Returns true if the given DRT can be closed.
     *
     * @param drt
     * @return boolean
     */
    @Override
    public boolean isClosable(Drt drt) {
        return isValidatable(drt) && drt.getStatus().equals(EnumStatus.VALIDATED);
    }

    /**
     * Set the DRT to validate
     *
     * @param drt
     */
    @Override
    public void validateDrt(Drt drt) {
        if (isValidatable(drt)) {
            drt.setStatus(EnumStatus.VALIDATED);
        }
    }

    /**
     * Returns true if the given DRT can be closed.
     *
     * @param drtId
     */
    @Override
    public void closeDrt(Long drtId) throws FunctionalException {
        Drt drt = drtRepository.findById(drtId).orElseThrow(() -> new NotFoundException("retex.error.drt.not.found"));
        if (isClosable(drt)) {
            drt.setStatus(EnumStatus.CLOSED);
            drt.getFiltering().setStatus(EnumStatus.CLOSED);
        } else {
            throw new FunctionalException("retex.error.drt.not.closable");
        }
    }
}



