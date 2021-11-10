package com.airbus.retex.service.impl.damage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.damage.DamageCreationDto;
import com.airbus.retex.business.dto.damage.DamageFullDto;
import com.airbus.retex.business.dto.damage.DamageLightDto;
import com.airbus.retex.business.dto.damage.functionality.FunctionalityDamageCreationDto;
import com.airbus.retex.business.dto.damage.functionality.FunctionalityDamageDto;
import com.airbus.retex.business.dto.damage.functionality.FunctionalityDamageUpdateDto;
import com.airbus.retex.business.dto.media.MediaDto;
import com.airbus.retex.business.dto.versioning.VersionDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.config.RetexConfig;
import com.airbus.retex.model.basic.CloningContext;
import com.airbus.retex.model.classification.EnumClassification;
import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.damage.Damage;
import com.airbus.retex.model.damage.DamageCloner;
import com.airbus.retex.model.damage.DamageTranslation;
import com.airbus.retex.model.functionality.damage.FunctionalityDamage;
import com.airbus.retex.model.functionality.damage.FunctionalityDamageTranslation;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.persistence.damage.DamageRepository;
import com.airbus.retex.persistence.functionality.FunctionalityRepository;
import com.airbus.retex.service.damage.IDamageService;
import com.airbus.retex.service.impl.damage.mapper.DamageMapper;
import com.airbus.retex.service.impl.media.MediaServiceImpl;
import com.airbus.retex.service.impl.translate.TranslationMapper;
import com.airbus.retex.service.impl.versionable.AbstractVersionableService;
import com.airbus.retex.service.routingComponent.IRoutingComponentService;

import liquibase.util.file.FilenameUtils;


@Service
@Transactional(rollbackFor = Exception.class)
public class DamageServiceImpl extends AbstractVersionableService<Damage, Long, DamageCreationDto> implements IDamageService {
    private static final String EXTENSION_FORMAT_NOT_ACCEPTED = "retex.error.media.format.denied";
    @Autowired
    private DamageRepository damageRepository;
    @Autowired
    private FunctionalityRepository functionalityRepository;

    @Autowired
    private DtoConverter dtoConverter;
    @Autowired
    private MediaServiceImpl mediaService;
    @Autowired
    private IRoutingComponentService routingComponentService;

    @Autowired
    private TranslationMapper translationMapper;
    @Autowired
    private DamageCloner damageCloner;
    @Autowired
    private DamageMapper damageMapper;
    @Autowired
    private RetexConfig retexConfig;



    /**
     * find a damages
     *
     * @param naturalId damage NaturalId
     * @param version current version of damage
     * @return Damage
     */
    @Override
    public Damage findByNaturalIdAndVersion(Long naturalId, Long version) throws NotFoundException {
        return damageRepository.findByNaturalIdAndVersion(naturalId, version)
                .orElseThrow(() -> new NotFoundException("retex.damage.notFound"));
    }

    /**
     * Create a damages
     *
     * @param creationDto dto for damage creation
     * @return DamageFullDto
     */
    @Override
    public DamageFullDto createDamage(DamageCreationDto creationDto, Language language) throws Exception {
        Damage damage = dtoConverter.createEntity(creationDto, Damage.class);
        damage.setStatus(EnumStatus.CREATED);
        translationMapper.updateEntityTranslations(damage, DamageTranslation::new, creationDto.getTranslatedFields());
        damage = createVersion(damage);

        return damageMapper.createDamageFullDto(damage);
    }

    /**
     * Add damage image
     *
     * @param damageNaturalId damage NaturalId
     * @param file
     * @return MediaDto
     * @throws FunctionalException
     */
    @Override
	public MediaDto addDamageImage(Long damageNaturalId, MultipartFile file) throws FunctionalException {
        String formatName = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!Arrays.asList(retexConfig.getPartAcceptedMediaFormats()).contains(formatName)) {
            throw new FunctionalException(EXTENSION_FORMAT_NOT_ACCEPTED);
        }
        //get damage images
        Media media =  mediaService.writeStream(file.getOriginalFilename(), file);
        //save damage
        Damage damage = updateVersion(damageNaturalId, damageToUpdate -> {
            damageToUpdate.setStatus(EnumStatus.CREATED);
            damageToUpdate.addImage(media);
        });

        return dtoConverter.toDto(damage.getImages().iterator().next(), MediaDto.class);
    }

    /**
     * Update a damage : fields, images, status ... Etc
     *
     * @param creationDto
     * @param naturalId damage NaturalId
     * @return DamageFullDto
     * @throws FunctionalException
     */
    @Override
    public DamageFullDto updateDamage(DamageCreationDto creationDto, Long naturalId) throws FunctionalException {
        //Check exist
        findByNaturalIdAndVersion(naturalId, null);

        // Update TranslatedFields
        Damage updatedVersion = updateVersion(naturalId, damageToUpdate -> {
            damageToUpdate.setStatus(EnumStatus.CREATED);
            translationMapper.updateEntityTranslations(damageToUpdate, DamageTranslation::new, creationDto.getTranslatedFields());
        });

        return damageMapper.createDamageFullDto(updatedVersion);
    }

    /**
     * Get one damage by Id
     *
     * @param naturalId damage NaturalId
     * @return DamageFullDto
     */
    @Override
    public DamageFullDto getDamage(Long naturalId, Long version) throws NotFoundException {
        Damage damage = findByNaturalIdAndVersion(naturalId, version);
        return damageMapper.createDamageFullDto(damage);
    }

    /**
     * Get all listed damages
     *
     * @param language user language
     * @param state State status
     * @return List<DamageLightDto>
     */
    @Override
    public List<DamageLightDto> getAllDamages(Language language, EnumActiveState state) {
        Specification<Damage> specification = Specification.where(null);
        specification = specification.and(isState(state));
        List<Damage> damages = damageRepository.findAllLastVersions(specification);
        return damageMapper.createDamageLightDto(damages);
    }

    private Specification<Damage> isState(EnumActiveState enumState){
        return (root, query, cb) -> cb.equal(root.get("state"), enumState);
    }

    /**
     * Revoke a damage
     *
     * @param naturalId damage natural Id
     */
    @Override
    public void deleteDamage(Long naturalId) throws FunctionalException {
        Damage damage = findByNaturalIdAndVersion(naturalId, null);

        if (!damage.getStatus().equals(EnumStatus.CREATED)
            && routingComponentService.isDamageLinkedToRoutingComponent(damage.getNaturalId())) {
            throw new FunctionalException("retex.damage.delete.link.routing.component");
        }

        deleteVersion(damage.getNaturalId());
    }

    /**
     * Delete damaged (affected) functionality of a damage
     *
     * @param damageNaturalId damage NaturalId
     * @param functionalityID functionality ID
     * @return int
     */
    @Override
    public int deleteDamagedFunctionalityOfDamage(Long damageNaturalId, Long functionalityID) throws FunctionalException {
        findByNaturalIdAndVersion(damageNaturalId, null);
        List<FunctionalityDamage> functionalityDamagesToDelete = new ArrayList<>();

        updateVersion(damageNaturalId, damageToUpdate -> {
            damageToUpdate.setStatus(EnumStatus.CREATED);

            for (FunctionalityDamage functionalityDamage : damageToUpdate.getFunctionalityDamages()) {
                if (functionalityDamage.getFunctionality().getId().equals(functionalityID)) {
                    functionalityDamagesToDelete.add(functionalityDamage);
                }
            }
            damageToUpdate.getFunctionalityDamages().removeAll(functionalityDamagesToDelete);
        });

        return functionalityDamagesToDelete.size();
    }

    /**
     * @param damageNaturalId damage NaturalId
     * @param functionalityID functionality ID
     * @return List<FunctionalityDamageDto>
     */
    @Override
    public List<FunctionalityDamageDto> getFunctionalityDamages(Long damageNaturalId, Long functionalityID, Long version) throws NotFoundException {
        Damage damage = findByNaturalIdAndVersion(damageNaturalId, version);

        List<FunctionalityDamage> functionalityDamageList = damage.getFunctionalityDamages().stream()
                .filter(functionalityDamage ->
                        functionalityDamage.getFunctionality().getId().equals(functionalityID)
                )
                .sorted(Comparator.comparing(functionalityDamage -> functionalityDamage.getNaturalId()))
                .collect(Collectors.toList());
        return damageMapper.createListFunctionalityDamageDto(functionalityDamageList);

    }

    /**
     * Revoke (deleteVersion) a given FunctionalityDamage of damage
     * @param damageNaturalId damage natural id
     * @param functionalityDamageID natural id of FunctionalityDamage
     */
    @Override
    public void deleteFunctionalityDamageOfDamage(Long damageNaturalId, Long functionalityDamageID) throws FunctionalException {

        findByNaturalIdAndVersion(damageNaturalId, null);
        updateVersion(damageNaturalId, damageToUpdate -> {
            damageToUpdate.setStatus(EnumStatus.CREATED);
            Optional<FunctionalityDamage> functionalityDamageOptional = damageToUpdate.getFunctionalityDamages().stream()
                    .filter(functionalityDamage -> functionalityDamage.getNaturalId().equals(functionalityDamageID)).findFirst();

            if (functionalityDamageOptional.isPresent()) {
                FunctionalityDamage functionalityDamage = functionalityDamageOptional.get();
                damageToUpdate.getFunctionalityDamages().remove(functionalityDamage);
            } else {
                throw new FunctionalException("retex.error.functionalityDamage.not.found");
            }
        });
    }

    /**
     * Add a functionalityDamage to a damage
     *
     * @param damageNaturalId NaturalId of damage
     * @param functionalityID Id of functionality
     * @param functionalityDamageDto id ofFunctionalityDamage
     */
    @Override
    public void addFunctionalityDamageToDamage(Long damageNaturalId, Long functionalityID, FunctionalityDamageCreationDto functionalityDamageDto) throws FunctionalException {
        // get the damage
        findByNaturalIdAndVersion(damageNaturalId, null);

        // save damage
       updateVersion(damageNaturalId, damage1 -> {
           damage1.setStatus(EnumStatus.CREATED);
           FunctionalityDamage currentFunctionalityDamage = new FunctionalityDamage();
           currentFunctionalityDamage.setDamage(damage1);  // set functionalityDamage's damage
           currentFunctionalityDamage.setState(EnumActiveState.ACTIVE); // by default a FunctionalityDamage is ACTIVE
           currentFunctionalityDamage.setFunctionality(functionalityRepository.getOne(functionalityID)); // set functionalityDamage's functionality
           // save translated fields of the FunctionalityDamage
           translationMapper.updateEntityTranslations(currentFunctionalityDamage, FunctionalityDamageTranslation::new, functionalityDamageDto.getTranslatedFields());
           damage1.getFunctionalityDamages().add(currentFunctionalityDamage); // set damage with new functionalityDamage
       });
    }

    /**
     * Add a functionalityDamage image to a damage
     *
     * @param damageNaturalId NaturalId of damage
     * @param functionalityDamageID natural id of functionalityDamage
     * @param file file to add
     */
    @Override
    public MediaDto addFunctionalityDamageImage(Long damageNaturalId, Long functionalityDamageID, MultipartFile file) throws FunctionalException {

        String formatName = FilenameUtils.getExtension(file.getOriginalFilename());
        Media media =  mediaService.writeStream(file.getOriginalFilename(), file);
        if (!Arrays.asList(retexConfig.getPartAcceptedMediaFormats()).contains(formatName)) {
            throw new FunctionalException(EXTENSION_FORMAT_NOT_ACCEPTED);
        }
        updateVersion(damageNaturalId, damage1 -> {
            damage1.setStatus(EnumStatus.CREATED);

            Optional <FunctionalityDamage> functionalityDamageOptional = damage1.getFunctionalityDamages().stream()
                    .filter(functionalityDamage -> functionalityDamage.getNaturalId().equals(functionalityDamageID))
                    .findFirst();
            if(functionalityDamageOptional.isPresent()) {
                FunctionalityDamage functionalityDamage = functionalityDamageOptional.get();
                functionalityDamage.setImage(media);
            }
        });

        return new MediaDto(media.getFilename(), media.getUuid());
    }

    /**
     * Update FunctionalityDamage of damage
     * @param damageNaturalId Damage natural Id
     * @param functionalityDamageID functionalityDamage naturalID
     * @param functionalityDamageUpdateDto translatedField to Update
     */
    @Override
    public DamageFullDto updateFunctionalityDamageOfDamage(Long damageNaturalId, Long functionalityDamageID, FunctionalityDamageUpdateDto functionalityDamageUpdateDto) throws FunctionalException {

        Damage damage = updateVersion(damageNaturalId, damageToUpdate -> {
            damageToUpdate.setStatus(EnumStatus.CREATED);

            Optional <FunctionalityDamage> functionalityDamageOptional = damageToUpdate.getFunctionalityDamages().stream()
                    .filter(functionalityDamage -> functionalityDamage.getNaturalId().equals(functionalityDamageID))
                    .findFirst();
            if(functionalityDamageOptional.isPresent()) {
                FunctionalityDamage functionalityDamage = functionalityDamageOptional.get();
                translationMapper.updateEntityTranslations(functionalityDamage, FunctionalityDamageTranslation::new, functionalityDamageUpdateDto.getTranslatedFields());
            }
        });
        return damageMapper.createDamageFullDto(damage);
    }

    /**
     * Delete image of damage
     * @param damageNaturalId Damage natural Id
     * @param uuidValue uuidValue of the media to delete
     */
    @Override
    public boolean deleteImage(Long damageNaturalId, String uuidValue) throws FunctionalException{
        updateVersion(damageNaturalId, damageToUpdate -> {
            damageToUpdate.setStatus(EnumStatus.CREATED);
            UUID uuid = UUID.fromString(uuidValue);
            Optional<Media> mediaOptional = damageToUpdate.getImages().stream()
                    .filter(media1 -> media1.getUuid().equals(uuid)).findFirst();
            if (mediaOptional.isPresent()){
                damageToUpdate.getImages().remove(mediaOptional.get());
            } else {
                throw new FunctionalException("retex.error.media.not.found");
            }
        });
        return true;
    }

    @Override
    public DamageFullDto updateStatus(Long damageID, boolean validate) throws FunctionalException {
        Damage damage = updateVersion(damageID, damageToUpdate -> {
            if(validate) {
                damageToUpdate.setStatus(EnumStatus.VALIDATED);
            } else {
                damageToUpdate.setStatus(EnumStatus.CREATED);
            }
        });
        return damageMapper.createDamageFullDto(damage);
    }

    @Override
    protected void mapDtoToVersion(DamageCreationDto updateDto, Damage version) {
        damageMapper.updateDamage(updateDto, version, new CloningContext());
    }

    @Override
    protected Damage cloneVersion(Damage version) {
        return damageCloner.cloneDamage(version, new CloningContext());
    }

    @Override
    public List<VersionDto> findAllVersionsByNaturalId(Long naturalId) {
        return dtoConverter.toDtos(damageRepository.findAllVersionsByNaturalId(naturalId), VersionDto.class);
    }
}
