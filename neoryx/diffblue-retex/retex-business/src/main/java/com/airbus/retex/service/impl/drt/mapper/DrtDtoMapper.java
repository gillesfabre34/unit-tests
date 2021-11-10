package com.airbus.retex.service.impl.drt.mapper;

import com.airbus.retex.business.dto.drt.DrtDto;
import com.airbus.retex.business.dto.drt.DrtHeaderDto;
import com.airbus.retex.business.dto.drtPicture.DrtPicturesDto;
import com.airbus.retex.business.dto.functionalArea.FunctionalAreaHeaderDto;
import com.airbus.retex.business.dto.inspection.InspectionDetailsDto;
import com.airbus.retex.business.dto.inspection.InspectionPostValueDto;
import com.airbus.retex.business.dto.inspection.InspectionStepActivationDto;
import com.airbus.retex.business.dto.inspection.InspectionValueDto;
import com.airbus.retex.business.dto.media.MediaDto;
import com.airbus.retex.business.dto.operationPost.RoutingFunctionalAreaPostLightDto;
import com.airbus.retex.business.dto.post.PostCustomDto;
import com.airbus.retex.business.dto.post.PostDrtCustomDto;
import com.airbus.retex.business.dto.step.StepActivationDrtDto;
import com.airbus.retex.business.dto.step.StepCustomDrtDto;
import com.airbus.retex.business.exception.ConstantExceptionRetex;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.mapper.AbstractMapper;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.control.*;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.drt.DrtOperationStatusFunctionalArea;
import com.airbus.retex.model.drt.DrtOperationStatusTodoList;
import com.airbus.retex.model.drt.DrtPictures;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.operation.OperationTypeBehaviorEnum;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.post.RoutingFunctionalAreaPost;
import com.airbus.retex.model.qcheck.QcheckRoutingComponent;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.step.StepActivation;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.persistence.admin.MediaRepository;
import com.airbus.retex.persistence.admin.MediaTemporaryRepository;
import com.airbus.retex.persistence.drt.DrtPicturesRepository;
import com.airbus.retex.service.impl.todoList.mapper.TodoListMapper;
import com.airbus.retex.service.impl.util.ClassUtil;
import com.airbus.retex.service.media.IMediaService;
import com.airbus.retex.service.translate.ITranslateService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class DrtDtoMapper extends AbstractMapper {

    @Autowired
    private ITranslateService translateService;
    @Autowired
    private TodoListMapper todoListMapper;

    @Autowired
    private DrtPicturesRepository drtPicturesRepository;

    @Autowired
    private IMediaService mediaService;
    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private MediaTemporaryRepository mediaTemporaryRepository;

    public abstract List<DrtDto> convert(Collection<Drt> drts);

    @Mapping(source = "filtering.physicalPart.part.partNumber", target = "partNumber")
    @Mapping(source = "filtering.physicalPart.serialNumber", target = "serialNumber")
    @Mapping(source = "filtering.physicalPart.part.partDesignation", target = "designation")
    @Mapping(source = "childRequest.parentRequest.origin", target = "origin")
    public abstract DrtDto convert(final Drt drt);


    @Mapping(source = "filtering.physicalPart.part.partNumber", target = "partNumber")
    @Mapping(source = "filtering.physicalPart.serialNumber", target = "serialNumber")
    @Mapping(source = "filtering.physicalPart.part.partDesignation", target = "designation")
    @Mapping(source = "childRequest.parentRequest.origin", target = "origin")
    @Mapping(source = "routing.naturalId", target = "routingId")
    @Mapping(source = "routing.versionNumber", target = "routingVersion")
    public abstract DrtHeaderDto convertToHeader(final Drt drt, @Context Language language);

    @AfterMapping
    protected void mapAdditionalHeaderValues(Drt drt, @MappingTarget DrtHeaderDto drtHeaderDto, @Context Language language) {
        ChildRequest childRequest = drt.getChildRequest(); // always not null
        drtHeaderDto.setAssociatedRequest(translateService.getFieldValue(Request.class.getSimpleName(),
                childRequest.getParentRequest().getId(), Request.FIELD_NAME, language));
        if (drt.getRouting() == null) {
            Routing routing = childRequest.getRouting();
            if (routing != null) {
                drtHeaderDto.setRoutingId(routing.getNaturalId());
                drtHeaderDto.setRoutingVersion(routing.getVersionNumber());
            }
        }
    }

    public abstract FunctionalAreaHeaderDto convertOperationTaskToHeader(final FunctionalArea functionalArea);

	@Mapping(source = "routingComponentIndex.naturalId", target = "idRoutingComponentIndex")
    @Mapping(source = "routingComponentIndex.versionNumber", target = "version")
    @Mapping(source = "damage", target = "damage")
    @Mapping(source = "operationType.behavior", target = "behavior")
    public abstract InspectionDetailsDto convertInspectionDetails(RoutingComponent routingComponent);

    @Mapping(source = "medias", target = "images")
    public abstract DrtPicturesDto convertToDrtPicturesDto(DrtPictures drtPictures);

    public abstract StepActivationDrtDto convertCustomStepActivationDto(StepActivation stepActivation);

    private StepActivationDrtDto convertCustomStepActivationDto(StepActivation stepActivation, Drt drt) throws FunctionalException {
        StepActivationDrtDto stepActivationDrtDto = convertCustomStepActivationDto(stepActivation);
        List<DrtPictures> drtPicturesList = drt.getDrtPictures().stream()
                .filter(drtPict -> drtPict.getStepActivation().getTechnicalId().equals(stepActivation.getTechnicalId()))
                .collect(Collectors.toList());
        if (drtPicturesList.size() > 1) {
            throw new FunctionalException("retex.error.drt.pictures.multiple.found");
        }
        if (!drtPicturesList.isEmpty()) {
            stepActivationDrtDto.setDrtPictures(convertToDrtPicturesDto(drtPicturesList.get(0)));
        }
        return stepActivationDrtDto;
    }

	@Mapping(source = "step.stepNumber", target = "stepNumber")
    @Mapping(source = "step.files", target = "files")
    @Mapping(source = "step.type", target = "type")
    public abstract void convertCustomStepDto(Step step, @MappingTarget StepCustomDrtDto stepCustomDrtDto);

    public List<StepCustomDrtDto> convertListCustomStepDto(List<StepActivation> stepActivations, Drt drt) throws FunctionalException {
        List<StepCustomDrtDto> stepCustomDtos = new ArrayList<>();
        for (StepActivation stepActivation : stepActivations) {
            StepCustomDrtDto stepCustomDrtDto = new StepCustomDrtDto();
            convertCustomStepDto(stepActivation.getStep(), stepCustomDrtDto);
            stepCustomDrtDto.setStepActivation(convertCustomStepActivationDto(stepActivation, drt));
            List<RoutingFunctionalAreaPost> routingFunctionalAreaPosts = stepActivation.getRoutingFunctionalAreaPosts()
                    .stream().sorted(Comparator.comparing(rfap -> rfap.getPost().getNaturalId()))
                    .collect(Collectors.toList());
            stepCustomDrtDto.setPosts(convertListCustomPostDto(routingFunctionalAreaPosts, drt,
                    stepActivation.getOperationFunctionalArea().getOperation().getOperationType().getBehavior()));
            stepCustomDtos.add(stepCustomDrtDto);
        }
        return stepCustomDtos;
    }


    @Mapping(source = "post.measureUnit", target = "measureUnit")
    public abstract void convertCustomPostDto(Post post, @MappingTarget PostCustomDto postCustomDto);

    public abstract RoutingFunctionalAreaPostLightDto convertRoutingFAPostDto(RoutingFunctionalAreaPost routingFunctionalAreaPost);

    private List<PostDrtCustomDto> convertListCustomPostDto(List<RoutingFunctionalAreaPost> routingFunctionalAreaPosts, Drt drt, OperationTypeBehaviorEnum behavior) {
        List<PostDrtCustomDto> postDrtCustomDtos = new ArrayList<>();
        if (behavior.equals(OperationTypeBehaviorEnum.ROUTING_COMPONENT)) {
            List<ControlRoutingComponent> routingComponentControls = getControls(drt, ControlRoutingComponent.class);
            routingFunctionalAreaPosts.forEach(routingFunctionalAreaPost -> addPostCustomDto(postDrtCustomDtos,
                    routingFunctionalAreaPost, routingComponentControls));
        } else {
            addPostCustomDto(postDrtCustomDtos, routingFunctionalAreaPosts.iterator().next(), getControls(drt, ControlVisual.class));
        }
        return postDrtCustomDtos;
    }

    private <T extends AbstractControlRoutingComponent> void addPostCustomDto(List<PostDrtCustomDto> postCustomDtos, RoutingFunctionalAreaPost routingFunctionalAreaPost, List<T> controls) {
        PostDrtCustomDto postCustomDto = new PostDrtCustomDto();
        convertCustomPostDto(routingFunctionalAreaPost.getPost(), postCustomDto);
        postCustomDto.setPostThreshold(convertRoutingFAPostDto(routingFunctionalAreaPost));
        postCustomDtos.add(setControlValue(controls, postCustomDto));
    }

    private <T extends AbstractControlRoutingComponent> PostDrtCustomDto setControlValue(List<T> controls, PostDrtCustomDto postCustomDto) {
        controls.stream()
                .filter(control -> control.getRoutingFunctionalAreaPost().getNaturalId().equals(postCustomDto.getPostThreshold().getId()))
				.findFirst().ifPresent(control -> postCustomDto.setValue(control.getValue()));
        return postCustomDto;
    }

    private <T extends AbstractControl> List<T> getControls(Drt drt, Class<T> controlClass) {
        return drt.getControls().stream()
                .filter(toto -> controlClass.isAssignableFrom(toto.getClass()))
                .map(abstractControl -> (T) abstractControl)
                .collect(Collectors.toList());
    }


    public void updateDrt(@MappingTarget Drt drt, Operation operation, Long taskId, InspectionValueDto inspectionValueDto, boolean validate) throws FunctionalException {
        if (operation.getOperationType().isBehavior(OperationTypeBehaviorEnum.TODO_LIST)) {
            updateDrtTodoList(drt, operation, taskId, inspectionValueDto);
            updateOperationStatusTodoList(drt, operation, taskId, validate);
        } else if (operation.getOperationType().isBehavior(OperationTypeBehaviorEnum.ROUTING_COMPONENT)) {
            updateDrtRoutingComponent(drt, operation, taskId, inspectionValueDto, ControlRoutingComponent.class, validate);
            updateOperationStatusRC(drt, operation, taskId, validate);
        } else if (operation.getOperationType().isBehavior(OperationTypeBehaviorEnum.VISUAL_COMPONENT)) {
            updateDrtRoutingComponent(drt, operation, taskId, inspectionValueDto, ControlVisual.class, validate);
            updateOperationStatusRC(drt, operation, taskId, validate);
        } else {
            throw new FunctionalException(ConstantExceptionRetex.OPERATION_TYPE_NOT_ALLOW);
        }
    }

    private void updateOperationStatusTodoList(Drt drt, Operation operation, Long taskId, boolean validate) throws FunctionalException {
        if (!operation.getOperationType().isBehavior(OperationTypeBehaviorEnum.TODO_LIST)) {
            throw new FunctionalException(ConstantExceptionRetex.OPERATION_TYPE_NOT_ALLOW);
        }

        EnumStatus status = validate ? EnumStatus.VALIDATED : EnumStatus.IN_PROGRESS;
        Optional<DrtOperationStatusTodoList> drtOperationStatusTodoListOptional = drt.getOperationStatus().stream()
                .filter(abstractDrtOperationStatus -> abstractDrtOperationStatus instanceof DrtOperationStatusTodoList)
                .map(abstractDrtOperationStatus -> (DrtOperationStatusTodoList) abstractDrtOperationStatus)
                .filter(drtOperationStatusTodoList -> drtOperationStatusTodoList.getTodoList().getNaturalId().equals(taskId)).findFirst();
        if (drtOperationStatusTodoListOptional.isPresent()) {
            if (drtOperationStatusTodoListOptional.get().getStatus().equals(EnumStatus.VALIDATED)) {
                throw new FunctionalException("retex.error.drt.inspection.wrong.status");
            }
            drtOperationStatusTodoListOptional.get().setStatus(status);

        } else {
            DrtOperationStatusTodoList drtOperationStatusTodoList = new DrtOperationStatusTodoList();
            drtOperationStatusTodoList.setStatus(status);
            drtOperationStatusTodoList.setOperation(operation);
            drtOperationStatusTodoList.setDrt(drt);
            Optional<TodoList> todoList = operation.getTodoLists().stream().filter(todoList1 -> todoList1.getNaturalId().equals(taskId)).findFirst();
            if (todoList.isPresent()) {
                drtOperationStatusTodoList.setTodoList(todoList.get());
            } else {
                throw new FunctionalException("retex.routing.component.todo.list.notExists");
            }
            drt.addOperationStatus(drtOperationStatusTodoList);
        }
    }

    private void updateOperationStatusRC(Drt drt, Operation operation, Long taskId, boolean validate) throws FunctionalException {
        EnumStatus status;
        if (operation.getOperationType().isBehavior(OperationTypeBehaviorEnum.ROUTING_COMPONENT)) {
            status = validate ? EnumStatus.Q_CHECK : EnumStatus.IN_PROGRESS;
        } else if(operation.getOperationType().isBehavior(OperationTypeBehaviorEnum.VISUAL_COMPONENT)) {
            status = validate ? EnumStatus.VALIDATED : EnumStatus.IN_PROGRESS;
        } else {
            throw new FunctionalException(ConstantExceptionRetex.OPERATION_TYPE_NOT_ALLOW);
        }

        Optional<DrtOperationStatusFunctionalArea> drtOperationStatusFunctionalAreaOptional = drt.getOperationStatus().stream()
                .filter(abstractDrtOperationStatus -> abstractDrtOperationStatus instanceof DrtOperationStatusFunctionalArea)
                .map(abstractDrtOperationStatus -> (DrtOperationStatusFunctionalArea) abstractDrtOperationStatus)
                .filter(drtOperationStatusFunctionalArea -> drtOperationStatusFunctionalArea.getOperationFunctionalArea().getOperation().getNaturalId().equals(operation.getNaturalId()))
                .filter(drtOperationStatusFunctionalArea -> drtOperationStatusFunctionalArea.getOperationFunctionalArea().getFunctionalArea().getNaturalId().equals(taskId)).findFirst();
        if (drtOperationStatusFunctionalAreaOptional.isPresent()) {
            if (drtOperationStatusFunctionalAreaOptional.get().getStatus().equals(EnumStatus.VALIDATED)) {
                throw new FunctionalException("retex.error.drt.inspection.wrong.status");
            }
            drtOperationStatusFunctionalAreaOptional.get().setStatus(status);
        } else {
            DrtOperationStatusFunctionalArea drtOperationStatusFunctionalArea = new DrtOperationStatusFunctionalArea();
            drtOperationStatusFunctionalArea.setStatus(status);
            drtOperationStatusFunctionalArea.setDrt(drt);

            Optional<OperationFunctionalArea> operationFunctionalArea = operation.getOperationFunctionalAreas().stream()
                    .filter(operationFunctionalArea1 -> operationFunctionalArea1.getFunctionalArea().getNaturalId().equals(taskId)).findFirst();
            if (operationFunctionalArea.isPresent()) {
                drtOperationStatusFunctionalArea.setOperationFunctionalArea(operationFunctionalArea.get());
            } else {
                throw new FunctionalException("retex.error.functional.area.not.found");
            }
            drt.addOperationStatus(drtOperationStatusFunctionalArea);
        }
    }

    private <T extends AbstractControlRoutingComponent> void updateDrtRoutingComponent(@MappingTarget Drt drt, Operation operation, Long taskId, InspectionValueDto inspectionValueDto, Class<T> controlClass, boolean validate) throws FunctionalException {
        List<T> controlRoutingComponents = getControls(drt, controlClass);
        Map<Long, QcheckRoutingComponent> qcheckRoutingComponentMap = getMapRoutingComponentIndexQcheck(drt);

        for (InspectionStepActivationDto step : inspectionValueDto.getSteps()) {
            List<InspectionPostValueDto> posts = step.getPosts();
            Map<Long, RoutingFunctionalAreaPost> routingFunctionalAreaPostMap = getRoutingFunctionalAreaPostMap(operation, taskId, posts);
            if (null != step.getPosts() && !step.getPosts().isEmpty()) {
                step.getPosts().forEach(inspectionPostValueDto -> {
                    Optional<T> controlRoutingComponentOptional = controlRoutingComponents.stream()
                            .filter(controlRoutingComponent -> controlRoutingComponent.getRoutingFunctionalAreaPost().getNaturalId().equals(inspectionPostValueDto.getPostThresholdId())).findFirst();
                    if (controlRoutingComponentOptional.isPresent()) {
                        if(!controlRoutingComponentOptional.get().getValue().equals(inspectionPostValueDto.getValue())) {
                            //CHECK IF QCHECK IS NOT TRUE
                            if(qcheckRoutingComponentMap.containsKey(step.getIdRoutingComponentIndex())
                                    && Boolean.TRUE.equals(qcheckRoutingComponentMap.get(step.getIdRoutingComponentIndex()).getValue())) {
                                throw new IllegalStateException("");
                            }
                            controlRoutingComponentOptional.get().setValue(inspectionPostValueDto.getValue());
                        }
                    } else {
                        T control = ClassUtil.instanciate(controlClass);
                        control.setValue(inspectionPostValueDto.getValue());
                        control.setDrt(drt);
                        control.setRoutingFunctionalAreaPost(routingFunctionalAreaPostMap.get(inspectionPostValueDto.getPostThresholdId()));
                        drt.addControl(control);
                    }
                });
            }
            // manage Drt pictures
            List<Long> stepActivationIds = drt.getDrtPictures().stream()
                    .map(drtPictures -> drtPictures.getStepActivation().getNaturalId())
                    .collect(Collectors.toList());

            DrtPictures drtPictures = null;
            if (!stepActivationIds.contains(step.getNaturalId())) {
                drtPictures = new DrtPictures();
                drtPictures.setDrt(drt);
                StepActivation stepActivation = getStepActivation(operation, taskId).stream()
                        .filter(stepAc -> stepAc.getNaturalId().equals(step.getNaturalId()))
                        .findFirst().orElseThrow(() -> new FunctionalException("retex.stepActivation.not.found"));
                drtPictures.setStepActivation(stepActivation);
            } else {
                drtPictures = drt.getDrtPictures().stream()
                        .filter(drtPics -> drtPics.getStepActivation().getNaturalId().equals(step.getNaturalId()))
                        .findFirst().orElseThrow(() -> new FunctionalException("retex.error.drt.pictures.not.found"));
            }
            List<UUID> mediaIds = new ArrayList<>();
            if (null != step.getDrtPictures() && !step.getDrtPictures().getImages().isEmpty()) {
                mediaIds = step.getDrtPictures().getImages().stream().map(MediaDto::getUuid).collect(Collectors.toList());
            }
            updateMediaList(mediaIds, drtPictures.getMedias());
            drt.addDrtPictures(drtPicturesRepository.save(drtPictures));

        }

        if(validate) {
            removeQcheck(drt, inspectionValueDto);
        }
    }

    /**
     *
     * @param drt drt
     * @param inspectionValueDto inspectionValueDto
     */
    private void removeQcheck(Drt drt, InspectionValueDto inspectionValueDto) {
        Map<Long, QcheckRoutingComponent> qcheckRoutingComponentMap = getMapRoutingComponentIndexQcheck(drt);
        List<Long> updatedRoutingComponentList = getDistinctRoutingComponent(inspectionValueDto);

        for (Long updatedRoutingComponent : updatedRoutingComponentList ) {
            if(qcheckRoutingComponentMap.containsKey(updatedRoutingComponent) &&
                    Boolean.FALSE.equals(qcheckRoutingComponentMap.get(updatedRoutingComponent).getValue())) { //If QCHECK IS TRUE
                qcheckRoutingComponentMap.get(updatedRoutingComponent).setValue(null);
            }
        }
    }

    /**
     *
     * @param drt drt
     * @param inspectionValueDto inspectionValueDto
     */
    private void checkQcheck(Drt drt, InspectionValueDto inspectionValueDto) throws FunctionalException {
        Map<Long, QcheckRoutingComponent> qcheckRoutingComponentMap = getMapRoutingComponentIndexQcheck(drt);
        List<Long> updatedRoutingComponentList = getDistinctRoutingComponent(inspectionValueDto);


        for (Long updatedRoutingComponent : updatedRoutingComponentList ) {
            if(qcheckRoutingComponentMap.containsKey(updatedRoutingComponent) &&
                    Boolean.TRUE.equals(qcheckRoutingComponentMap.get(updatedRoutingComponent).getValue())) { //If QCHECK IS TRUE
                throw new FunctionalException("retex.error.drt.qcheck.change.validated.value"); //TODO MESSAGE ERREUR
            }
        }

    }

    private Map<Long, QcheckRoutingComponent> getMapRoutingComponentIndexQcheck(Drt drt) {
        return drt.getQCheckRoutingComponents().stream()
                .filter(qcheckRoutingComponent -> qcheckRoutingComponent.getValue() != null)
                .collect(Collectors.toMap(qcheckRoutingComponent -> qcheckRoutingComponent.getRoutingComponentIndex().getNaturalId(), o -> o));
    }

    private List<Long> getDistinctRoutingComponent(InspectionValueDto inspectionValueDto) {
        if(null !=  inspectionValueDto.getSteps() && !inspectionValueDto.getSteps().isEmpty()) {
            return inspectionValueDto.getSteps().stream()
                .filter(inspectionStepActivationDto -> inspectionStepActivationDto.getIdRoutingComponentIndex() != null)
                .map(InspectionStepActivationDto::getIdRoutingComponentIndex)
                .distinct()
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


    private Map<Long, RoutingFunctionalAreaPost> getRoutingFunctionalAreaPostMap(Operation operation, Long taskId, List<InspectionPostValueDto> posts) throws FunctionalException {
        Map<Long, RoutingFunctionalAreaPost> mapRoutingFunctionalAreaPost = new HashMap<>();
        Set<StepActivation> stepActivations = getStepActivation(operation, taskId);
        stepActivations.forEach(stepActivation ->
            stepActivation.getRoutingFunctionalAreaPosts().forEach(routingFunctionalAreaPost -> {
            if (null != posts && !posts.isEmpty()) {
                Optional<InspectionPostValueDto> inspectionPostValueDtoOptional = posts.stream()
                        .filter(inspectionPostValueDto -> inspectionPostValueDto.getPostThresholdId()
                                .equals(routingFunctionalAreaPost.getNaturalId())).findFirst();
                inspectionPostValueDtoOptional.ifPresent(inspectionPostValueDto -> mapRoutingFunctionalAreaPost.put(inspectionPostValueDto.getPostThresholdId(), routingFunctionalAreaPost));
            }
        }));
        return mapRoutingFunctionalAreaPost;
    }


    Set<StepActivation> getStepActivation(Operation operation, Long taskId) throws FunctionalException {
        Optional<OperationFunctionalArea> operationFunctionalAreaOptional = operation.getOperationFunctionalAreas().stream()
                .filter(operationFunctionalArea -> operationFunctionalArea.getFunctionalArea().getNaturalId().equals(taskId)).findFirst();
        if (operationFunctionalAreaOptional.isPresent()) {
            return operationFunctionalAreaOptional.get().getStepActivations();
        } else {
            throw new FunctionalException("retex.operation.functionalarea.association.not.found");
        }
    }

    private void updateDrtTodoList(@MappingTarget Drt drt, Operation operation, Long taskId, InspectionValueDto inspectionValueDto) throws FunctionalException {
        List<ControlTodoList> controlTodoLists = getControls(drt, ControlTodoList.class);
        controlTodoLists = controlTodoLists.stream().filter(controlTodoList -> controlTodoList.getOperation().getNaturalId().equals(operation.getNaturalId())
                && controlTodoList.getTodoList().getNaturalId().equals(taskId))
                .collect(Collectors.toList());

        if (controlTodoLists.size() == 1) {
            controlTodoLists.get(0).setValue(inspectionValueDto.getTodoListValue());
        }
        if (controlTodoLists.isEmpty()) { // si pas de valeur initialisé
            ControlTodoList controlTodoList = new ControlTodoList(todoListMapper.getTodoListFromOperationAndTaskId(operation, taskId), operation, inspectionValueDto.getTodoListValue());
            controlTodoList.setDrt(drt);
            drt.addControl(controlTodoList);
        }
    }

    Operation getOperation(Drt drt, Long operationId) throws FunctionalException {
        List<Operation> operations = drt.getRouting().getOperations().stream()
                .filter(operation -> operation.getNaturalId().equals(operationId)).collect(Collectors.toList());
        if (operations.size() > 1) {
            throw new FunctionalException("retex.error.drt.operation.found.not.unique");
        }
        if (operations.isEmpty()) {
            throw new FunctionalException("retex.error.drt.operation.not.found");
        }
        return operations.get(0);
    }

}