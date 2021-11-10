package com.airbus.retex.service.impl.routingComponent.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.dto.post.PostCreationDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentCreateUpdateDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFullDto;
import com.airbus.retex.business.dto.step.StepCreationDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.StepType;
import com.airbus.retex.model.mesureUnit.MeasureUnit;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.operation.OperationTypeBehaviorEnum;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.post.PostTranslation;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.persistence.measureUnit.MeasureUnitRepository;
import com.airbus.retex.service.routingComponent.IRoutingComponentHandlerService;
import com.airbus.retex.service.routingComponent.IRoutingComponentService;

/**
 * In this case, we are going to create a dimensional RoutingComponent
 * Dimensional => Task is a functionalities
 * Dimensional => SubTask is a damagies
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoutingComponentHandler extends AbstractHandler implements IRoutingComponentHandlerService {

    @Autowired
    private MeasureUnitRepository measureUnitRepository;
    @Autowired
    private IRoutingComponentService routingComponentService;

    /**
     * @inheritDoc
     */
    @Override
    public RoutingComponentFullDto handle(RoutingComponentCreateUpdateDto routingComponentCreation, Boolean validated,
                                          @Nullable RoutingComponentIndex existingRoutingComponentIndexToUpdate) throws FunctionalException {
        routingComponentCreation.setStatus(validated ? EnumStatus.VALIDATED : EnumStatus.CREATED);

        RoutingComponentIndex routingComponentIndex = createOrUpdateRoutingComponentIndexedEntry(routingComponentCreation, existingRoutingComponentIndexToUpdate);

        return routingComponentService.buildRoutingComponentDto(routingComponentIndex,
                routingComponentIndex.getRoutingComponent(),
                routingComponentIndex.getRoutingComponent().getInspection().getValue(),
                routingComponentIndex.getRoutingComponent().getFunctionality().getId(),
                routingComponentIndex.getRoutingComponent().getDamageId(),
                routingComponentIndex.getNaturalId());
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean supports(OperationType operationType) {
        return operationType.isBehavior(OperationTypeBehaviorEnum.ROUTING_COMPONENT);
    }

    /**
     * return steps of routingComponent
     *
     * @param routingComponent
     * @param steps
     * @return
     */
    private void saveSteps(RoutingComponent routingComponent, List<StepCreationDto> steps, EnumStatus status) throws FunctionalException {
        int manualStepIndex = 1;
        int autoStepIndex = 1;

        // retrieve and deleteVersion old Step if any FIXME : si pas de nouvelle version
        if (CollectionUtils.isNotEmpty(routingComponent.getSteps())) {
            List<Long> newStepsId = steps
                    .stream()
                    .filter(p -> p.getNaturalId() != null)
                    .map(StepCreationDto::getNaturalId)
                    .collect(Collectors.toList());
            this.deleteOldSteps(routingComponent, newStepsId);
        }

        boolean alreadyExist;
        for (StepCreationDto stepCreationDto : steps) {
            alreadyExist = false;
            if (CollectionUtils.isNotEmpty(routingComponent.getSteps())) {
                for(Step oldStep : routingComponent.getSteps()) {
                    if(null != stepCreationDto.getNaturalId() && stepCreationDto.getNaturalId().equals(oldStep.getNaturalId())){ //Update d'une step existante
                        alreadyExist = true;

                        oldStep.setRoutingComponent(routingComponent);
                        oldStep.setType(stepCreationDto.getType());
                        oldStep.setStepNumber(stepCreationDto.getType().equals(StepType.AUTO) ? autoStepIndex++ : manualStepIndex++);
                        if(status.equals(EnumStatus.VALIDATED)){
                            oldStep.setDeletable(Boolean.FALSE);
                        } else {
                            oldStep.setDeletable(stepCreationDto.getIsDeletable());
                        }
                        oldStep.addFiles(saveAdditionalInformations(oldStep, stepCreationDto.getMediaUuids(), stepCreationDto.getTranslatedFields()));


                        savePosts(stepCreationDto.getPosts(), oldStep, status);
                    }
                }
            }

            //Creation d'un nouvelle step
            if(!alreadyExist){
                Step newStep = new Step();
                newStep.setType(stepCreationDto.getType());
                newStep.setStepNumber(stepCreationDto.getType().equals(StepType.AUTO) ? autoStepIndex++ : manualStepIndex++);
                if(status.equals(EnumStatus.VALIDATED)){
                    newStep.setDeletable(Boolean.FALSE);
                } else if(status.equals(EnumStatus.CREATED)) {
                    newStep.setDeletable(Boolean.TRUE);
                }
                newStep.addFiles(saveAdditionalInformations(newStep, stepCreationDto.getMediaUuids(), stepCreationDto.getTranslatedFields()));
                savePosts(stepCreationDto.getPosts(), newStep, status);

                if(null == routingComponent.getSteps()){
                    routingComponent.setSteps(new ArrayList<>());
                }
                routingComponent.addStep(newStep); // Ajout de la nouvelle step

            }
        }
    }

    /**
     * Save step's posts
     *
     * @param postCreationDtos
     * @param step
     */
    private void savePosts(List<PostCreationDto> postCreationDtos, Step step, EnumStatus status) {
        List<PostCreationDto> postCreationDtoLists = null != postCreationDtos ? postCreationDtos : new ArrayList<>();

        // retrieve and deleteVersion old posts if any FIXME : si pas de nouvelle version
        if (CollectionUtils.isNotEmpty(step.getPosts())) {
            List<Long> newPostsId = postCreationDtoLists
                    .stream()
                    .filter(p -> p.getNaturalId() != null)
                    .map(PostCreationDto::getNaturalId)
                    .collect(Collectors.toList());
            this.deleteOldPosts(step, newPostsId);
        }


        for(PostCreationDto postDto: postCreationDtoLists) {
            if (postDto.getNaturalId() != null) { // si déjà un id on cherche équivalent
                for (Post oldPost : step.getPosts()){
                    if(oldPost.getNaturalId().equals(postDto.getNaturalId())) {
                        Optional<MeasureUnit> measureUnitOptional = measureUnitRepository.findById(postDto.getMeasureUnitId());
                        if (measureUnitOptional.isPresent() && !measureUnitOptional.get().getId().equals(oldPost.getNaturalId())) {
                            oldPost.setMeasureUnit(measureUnitOptional.get());
                            oldPost.setMeasureUnitId(measureUnitOptional.get().getId());
                            if(status.equals(EnumStatus.VALIDATED)){
                                oldPost.setDeletable(Boolean.FALSE);
                            } else {
                                oldPost.setDeletable(postDto.getIsDeletable());
                            }
                        }
                        translationMapper.updateEntityTranslations(oldPost, PostTranslation::new, postDto.getTranslatedFields());
                    }
                }
            } else {
                // Création d'un nouveau post
                Post newPost = new Post();
                Optional<MeasureUnit> measureUnitOptional = measureUnitRepository.findById(postDto.getMeasureUnitId());
                if (measureUnitOptional.isPresent()) {
                    newPost.setMeasureUnit(measureUnitOptional.get());
                    newPost.setMeasureUnitId(measureUnitOptional.get().getId());
                    if(status.equals(EnumStatus.VALIDATED)){
                        newPost.setDeletable(Boolean.FALSE);
                    } else if(status.equals(EnumStatus.CREATED)) {
                        newPost.setDeletable(Boolean.TRUE);
                    }
                }
                step.addPost(newPost);
                translationMapper.updateEntityTranslations(newPost, PostTranslation::new, postDto.getTranslatedFields());
            }
        }
    }

    private void deleteOldPosts(Step step, List<Long> newPostsId) {
        Set<Post> postToRemove = step.getPosts()
                .stream()
                .filter(e -> !newPostsId.contains(e.getNaturalId()))
                .collect(Collectors.toSet());
        step.getPosts().removeAll(postToRemove);
    }

    private void deleteOldSteps(RoutingComponent routingComponent, List<Long> newStepsId) {
        Set<Step> stepsToRemove = routingComponent.getSteps()
                .stream()
                .filter(e -> !newStepsId.contains(e.getNaturalId()))
                .collect(Collectors.toSet());
        routingComponent.getSteps().removeAll(stepsToRemove);
    }

    protected void prepareRoutingComponentForUpdateOrCreate(RoutingComponent routingComponent, RoutingComponentCreateUpdateDto routingComponentCreation) throws FunctionalException {
        addOperationType(routingComponentCreation, routingComponent);
        addFunctionality(routingComponentCreation, routingComponent);
        addDamage(routingComponentCreation, routingComponent);
        addInspection(routingComponentCreation, routingComponent);
    }

    @Override
    protected void mapDtoToVersion(RoutingComponentCreateUpdateDto updateDto, RoutingComponentIndex version) throws FunctionalException {
        version.setStatus(updateDto.getStatus());
        prepareRoutingComponentForUpdateOrCreate(version.getRoutingComponent(), updateDto);
        // save different given steps
        if (CollectionUtils.isNotEmpty(updateDto.getSteps())) {
            saveSteps(version.getRoutingComponent(), updateDto.getSteps(), updateDto.getStatus());
        }
    }

}
