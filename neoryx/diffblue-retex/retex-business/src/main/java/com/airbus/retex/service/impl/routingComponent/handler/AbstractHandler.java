package com.airbus.retex.service.impl.routingComponent.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentCreateUpdateDto;
import com.airbus.retex.business.dto.step.StepCreationDto;
import com.airbus.retex.business.dto.versioning.VersionDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.mapper.CloningContext;
import com.airbus.retex.config.RetexConfig;
import com.airbus.retex.model.IRoutingComponentModel;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.common.StepType;
import com.airbus.retex.model.damage.Damage;
import com.airbus.retex.model.functionality.Functionality;
import com.airbus.retex.model.inspection.Inspection;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.step.StepFieldsEnum;
import com.airbus.retex.model.step.StepTranslation;
import com.airbus.retex.persistence.admin.MediaRepository;
import com.airbus.retex.persistence.damage.DamageRepository;
import com.airbus.retex.persistence.functionality.FunctionalityRepository;
import com.airbus.retex.persistence.inspection.InspectionRepository;
import com.airbus.retex.persistence.operationType.OperationTypeRepository;
import com.airbus.retex.persistence.routingComponent.RoutingComponentIndexRepository;
import com.airbus.retex.service.impl.routingComponent.mapper.RoutingComponentIndexCloner;
import com.airbus.retex.service.impl.translate.TranslationMapper;
import com.airbus.retex.service.impl.versionable.AbstractVersionableService;
import com.airbus.retex.service.media.IMediaService;

public abstract class AbstractHandler
        extends AbstractVersionableService<RoutingComponentIndex, Long, RoutingComponentCreateUpdateDto> {
    private static final String EXTENSION_FORMAT_NOT_ACCEPTED = "retex.error.media.format.denied";

    @Autowired
    protected RoutingComponentIndexRepository routingComponentIndexRepository;
    @Autowired
    protected RoutingComponentIndexCloner routingComponentIndexCloner;
    @Autowired
    private InspectionRepository inspectionRepository;
    @Autowired
    private DtoConverter dtoConverter;
    @Autowired
    private OperationTypeRepository operationTypeRepository;
    @Autowired
    private IMediaService iMediaService;
    @Autowired
    private FunctionalityRepository functionalityRepository;
    @Autowired
    private DamageRepository damageRepository;
    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    protected TranslationMapper translationMapper;
    @Autowired
    private RetexConfig retexConfig;

    /**
     * Each creation of RoutingComponent or of TodoList, an entry should be saved on table
     * RoutingComponentIndex : we need it to make pagination (when we have to retreive all the routingComponent list)
     *
     * @param routingComponentCreation
     * @param existingRoutingComponentIndexToUpdate
     * @return RoutingComponentIndex
     */
    protected RoutingComponentIndex createOrUpdateRoutingComponentIndexedEntry(@Nullable RoutingComponentCreateUpdateDto routingComponentCreation,
                                                                               @Nullable RoutingComponentIndex existingRoutingComponentIndexToUpdate) throws FunctionalException {

        RoutingComponentIndex routingComponentIndex = null;
        if (null != existingRoutingComponentIndexToUpdate) { //UPDATE
            routingComponentIndex = updateVersion(existingRoutingComponentIndexToUpdate.getNaturalId(), routingComponentIndex1 -> {
                mapDtoToVersion(routingComponentCreation, routingComponentIndex1);
            });
        } else { //Creation nouveau

            routingComponentIndex = new RoutingComponentIndex();
            routingComponentIndex.setRoutingComponent(new RoutingComponent());
            mapDtoToVersion(routingComponentCreation, routingComponentIndex);
            routingComponentIndex = createVersion(routingComponentIndex);

        }
        return routingComponentIndex;
    }

    /**
     * @param routingComponentCreation
     * @param component
     * @return
     */
    protected Optional<Inspection> addInspection(RoutingComponentCreateUpdateDto routingComponentCreation, IRoutingComponentModel component) {
        Optional<Inspection> inspectionOptional = inspectionRepository.findByValue(routingComponentCreation.getInspectionValue());
        if (inspectionOptional.isPresent()) {
            component.setInspection(inspectionOptional.get());
        }
        return inspectionOptional;
    }

    /**
     * @param routingComponentCreation
     * @param component
     * @return
     */
    protected Optional<OperationType> addOperationType(RoutingComponentCreateUpdateDto routingComponentCreation, IRoutingComponentModel component) {
        Optional<OperationType> operationType = operationTypeRepository.findById(routingComponentCreation.getOperationTypeId());
        if (operationType.isPresent()) {
            component.setOperationType(operationType.get());
        }
        return operationType;
    }

    /**
     * For RoutingComponent of type Generic, TodoList and Visual RoutingComponent
     * we create an empty step (with empty posts)
     *
     * @return
     */
    protected void associateEmptyStepAndAdditionalInformations(StepCreationDto stepCreationDto, RoutingComponentIndex routingComponentIndex) throws FunctionalException {
        Step step = new Step();
        if (null != routingComponentIndex.getTodoList()) {
            if (null == routingComponentIndex.getTodoList().getSteps() || routingComponentIndex.getTodoList().getSteps().isEmpty()) {
                step.setTodoList(routingComponentIndex.getTodoList());
                routingComponentIndex.getTodoList().addStep(step);
            }
            createOrupdateEmptyStep(stepCreationDto, routingComponentIndex.getTodoList().getSteps().get(0));
        } else if (null != routingComponentIndex.getRoutingComponent() ||
                    (
                            null == routingComponentIndex.getRoutingComponent().getSteps()
                                    && routingComponentIndex.getRoutingComponent().getSteps().isEmpty()
                    )
        ) {
            if (null == routingComponentIndex.getRoutingComponent().getSteps()|| routingComponentIndex.getRoutingComponent().getSteps().isEmpty()) {
                step.setRoutingComponent(routingComponentIndex.getRoutingComponent());
                List<Step> stepList = new ArrayList<>();
                stepList.add(step);
                routingComponentIndex.getRoutingComponent().setSteps(stepList);
            }
            createOrupdateEmptyStep(stepCreationDto, routingComponentIndex.getRoutingComponent().getSteps().get(0));
        }
    }

    private void createOrupdateEmptyStep(StepCreationDto stepCreationDto, Step step) throws FunctionalException {

        // save empty step informations and media
        if (null != stepCreationDto) {
            step.addFiles(saveAdditionalInformations(step, stepCreationDto.getMediaUuids(),
                    stepCreationDto.getTranslatedFields()));
        } else {
            throw new FunctionalException("retex.error.save.additional.informations");
        }
        if(stepCreationDto.getNaturalId() == null) {
            // set by default to AUTO
            step.setType(StepType.AUTO);
            // not necessary
            step.setStepNumber(1);
        }

    }

    /**
     * Save additional information's of routing component : media and information
     *
     * @param uuidFiles
     * @param translatedFields
     */
    protected Set<Media> saveAdditionalInformations(Step entityStep, List<String> uuidFiles,
                                                     Map<Language, Map<StepFieldsEnum, String>> translatedFields) throws FunctionalException {
        Set<Media> mediasToSave = new HashSet<>();

        if (CollectionUtils.isNotEmpty(entityStep.getFiles())) {
            List<String> newFilesUuid = uuidFiles
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            this.deleteOldFiles(entityStep, newFilesUuid);
        }

        if (CollectionUtils.isNotEmpty(uuidFiles)) {
            for (String uuid : uuidFiles) {
                Optional<Media> media = iMediaService.temporaryToPermanentMedia(UUID.fromString(uuid));
                // make sure that the updated media is the same as the given one(uuid)
                if (media.isPresent() && media.get().getUuid() !=null && media.get().getUuid().equals(UUID.fromString(uuid))) {
                    mediasToSave.add(media.get());
                } else {
                    // if uuid of the current file is not found in media_tmp, we check if exists in media
                    Optional<Media> mediaOptional = mediaRepository.findById(UUID.fromString(uuid));
                    if(mediaOptional.isPresent()){
                        String formatName = FilenameUtils.getExtension(mediaOptional.get().getFilename());
                        if (!Arrays.asList(retexConfig.getPartAcceptedMediaFormats()).contains(formatName)) {
                            throw new FunctionalException(EXTENSION_FORMAT_NOT_ACCEPTED);
                        }

                        mediasToSave.add(mediaOptional.get());
                    }
                }
            }
        }
        translationMapper.updateEntityTranslations(entityStep, StepTranslation::new, translatedFields);
        return mediasToSave;
    }

    private void deleteOldFiles(Step step, List<String> newUuid) {
        Set<Media> mediaToRemove = step.getFiles()
                .stream()
                .filter(e -> !newUuid.contains(e.getUuid().toString()))
                .collect(Collectors.toSet());
        step.getFiles().removeAll(mediaToRemove);
    }

    /**
     * set subTask : it's a dimensional routing component, so the subTask is a damage
     *
     * @param routingComponentCreation
     * @param routingComponent
     * @return
     * @throws FunctionalException
     */
    protected Optional<Damage> addDamage(RoutingComponentCreateUpdateDto routingComponentCreation, RoutingComponent routingComponent) throws FunctionalException {
        Damage damage = damageRepository.findValidatedVersionByNaturalId(routingComponentCreation.getSubTaskId()).orElseThrow(() -> new FunctionalException("retex.routing.component.damage.notExists"));

        routingComponent.setDamage(damage);
        routingComponent.setDamageId(damage.getNaturalId());
        return Optional.of(damage);
    }

    /**
     * set task : it's a dimensional routing component, so the task is a functionality
     *
     * @param routingComponentCreation
     * @param routingComponent
     * @return
     * @throws FunctionalException
     */
    protected Optional<Functionality> addFunctionality(RoutingComponentCreateUpdateDto routingComponentCreation, RoutingComponent routingComponent) throws FunctionalException {
        Functionality functionality = functionalityRepository.findById(routingComponentCreation.getTaskId()).orElseThrow(() -> new FunctionalException("retex.routing.component.functionality.notExists"));

        routingComponent.setFunctionality(functionality);
        return Optional.of(functionality);
    }

    @Override
    protected RoutingComponentIndex cloneVersion(RoutingComponentIndex version) {
        return routingComponentIndexCloner.cloneRoutingComponentIndex(version, new CloningContext());
    }


    @Override
    public List<VersionDto> findAllVersionsByNaturalId(Long naturalId) {
        List<RoutingComponentIndex> routingComponentIndexList = routingComponentIndexRepository.findAllVersionsByNaturalId(naturalId);
        return  dtoConverter.toDtos(routingComponentIndexList, VersionDto.class);
    }

}
