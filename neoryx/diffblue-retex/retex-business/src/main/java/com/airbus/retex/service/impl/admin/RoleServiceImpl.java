package com.airbus.retex.service.impl.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.feature.FeatureRightDto;
import com.airbus.retex.business.dto.role.RoleCreationUpdateDto;
import com.airbus.retex.business.dto.role.RoleDto;
import com.airbus.retex.business.dto.role.RoleFieldEnum;
import com.airbus.retex.business.dto.role.RoleFullDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.TechnicalError;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.airbus.AirbusEntity;
import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.model.admin.role.RoleFeature;
import com.airbus.retex.model.admin.specifications.RoleSpecification;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.persistence.admin.RoleFeatureRepository;
import com.airbus.retex.persistence.admin.RoleRepository;
import com.airbus.retex.persistence.airbus.AirbusEntityRepository;
import com.airbus.retex.service.admin.IRoleService;
import com.airbus.retex.service.translate.ITranslateService;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoleServiceImpl implements IRoleService {
    private static final String RETEX_ROLE_ERROR_LABEL = "retex.role.error.label";
	@Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleFeatureRepository roleFeatureRepository;
    @Autowired
    private ITranslateService translateService;
    @Autowired
    private DtoConverter dtoConverter;
    @Autowired
    private AirbusEntityRepository airbusEntityRepository;

    @Override
    @Secured("ROLE_ADMIN:READ")
    public List<RoleDto> getAllRolesByAirbusEntity(Long airbusEntityId) {
        return getAllRolesByAirbusEntityAndLanguage(airbusEntityId, null);
    }

    /**
     * @param airbusEntityId
     * @param locale
     * @return extract Roles matching airbusEntity and isoCode
     */

    // FIXME : remonter tous les roles filtré sur l'entityID et la langue
    @Override
    @Secured("ROLE_ADMIN:WRITE")
    public List<RoleDto> getAllRolesByAirbusEntityAndLanguage(Long airbusEntityId, Locale locale) {
		Specification<Role> specification = Specification
                .where(RoleSpecification.filterByAirbusEntity(airbusEntityId));

        if (locale != null) specification.and(RoleSpecification.filterByIsoCode(Language.languageFor(locale)));

        // FIXME : I REMOVED SPECIFICATION : WE NEED IT ?
        //List<Role> rolesWithSpec = roleRepository.findAllVersionsByNaturalId(specification);
        List<Role> rolesWithSpec = roleRepository.findByAirbusEntityId(airbusEntityId);

        return dtoConverter.convert(rolesWithSpec, RoleDto::new);
    }

    /**
     * ATTENTION : we can create Role with already used Label provided that Role hasn't same AirbusEntityID
     * So : we can't create Role with already existing AirbusEntityID and role label
     *
     * @param roleCreationDto
     * @return the light versioning of the created role
     * @throws TechnicalError
     */
    @Override
    @Secured("ROLE_ADMIN:WRITE")
    public RoleFullDto createRole(RoleCreationUpdateDto roleCreationDto) throws TechnicalError, FunctionalException {

        if (!this.anyRoleWithSameLabelAndAirbusEntity(roleCreationDto)) {
            Role role = dtoConverter.toEntity(roleCreationDto, Role::new);
            Optional<AirbusEntity> airbusEntity = airbusEntityRepository.findById(roleCreationDto.getAirbusEntity().getId());

            if (airbusEntity.isPresent()) {
                // set airbus entity
                role.setAirbusEntity(airbusEntity.get());
                // we will save feature separately
                role.setFeatures(null);
                // save role
                Role savedRole = roleRepository.save(role);

                // save RoleFeature of this role
                List<FeatureRightDto> featuresToSave = roleCreationDto.getFeatures();
                Collection<RoleFeature> savedRoleFeatures = new ArrayList<>();

                // save translated label of this role
                translateService.saveTranslatedFields(savedRole, roleCreationDto.getTranslatedFields());

                roleRepository.detach(role);

                for (FeatureRightDto rf :featuresToSave){
                    RoleFeature roleFeatureToSave = new RoleFeature(savedRole.getId(), rf.getCode(), rf.getRightLevel(), savedRole);
                    savedRoleFeatures.add(roleFeatureRepository.save(roleFeatureToSave));
                }

                return buildCreateUpdateRequestResult(savedRole, savedRoleFeatures);
            } else {
                // FIXME custom error response : airbus entity can't be null
                throw new FunctionalException(RETEX_ROLE_ERROR_LABEL);
            }
        } else {
            throw new FunctionalException(RETEX_ROLE_ERROR_LABEL);
        }
    }

    private RoleFullDto buildCreateUpdateRequestResult(Role savedRole, Collection<RoleFeature> savedRoleFeatures){
        RoleFullDto result = dtoConverter.toDto(roleRepository.getOne(savedRole.getId()), RoleFullDto.class);
        List<FeatureRightDto> featureRightDtoList = new ArrayList<>();
        savedRoleFeatures.forEach(roleFeature ->
                featureRightDtoList.add(new FeatureRightDto(roleFeature.getCode(), roleFeature.getRightLevel()))
        );
        result.setFeatures(featureRightDtoList);
        result.setTranslatedLabels(translateService.getFieldValues(savedRole, Role.FIELD_LABEL));
        return result;
    }



    /**
     * we can create Role with already used Label provided that Role hasn't same AirbusEntityID
     * So, we can't create Role with already existing AirbusEntityID and role label
     * //FIXME :  trop complexe, à réduire
     *
     * @param roleCreationDto
     * @return
     */
    private boolean anyRoleWithSameLabelAndAirbusEntity(RoleCreationUpdateDto roleCreationDto) {
        AtomicReference<Boolean> roleLabelAndEntityIdExists = new AtomicReference<>(false);
        // get list of roles of the given airbusEntityID
        List<Role> findRoles = roleRepository.findByAirbusEntityId(roleCreationDto.getAirbusEntity().getId());
        findRoles.stream().forEach(currentRole -> {
            // for each role, convert it to dto
            RoleFullDto currentRoleFullDto = this.getRole(currentRole.getId());
            // check if current label already exists in the roleCreationDto
			currentRoleFullDto.getTranslatedLabels().forEach((k, v) ->
                roleCreationDto.getTranslatedFields().forEach((createLanguage, value) -> {
                if (k.equals(createLanguage) && v.equals(value.get(RoleFieldEnum.label))) {
                        roleLabelAndEntityIdExists.set(true);
                        return;
                    }
			}));
        });
        return roleLabelAndEntityIdExists.get();
    }

    /**
     * @param updatedDto Role update Dto received from the client
     * @return light versioning of the updated role
     */
    @Override
    @Secured("ROLE_ADMIN:WRITE")
    public RoleFullDto updateRole(Long id, RoleCreationUpdateDto updatedDto) throws Exception {
        Role receivedRole = roleRepository.getOne(id);
        if (receivedRole == null) {
            // FIXME : soulever une expcetion (ALEX API ERROR) : ROLE NOT FOUND
            throw new FunctionalException(RETEX_ROLE_ERROR_LABEL);
        }
        // save new translations of the label
        translateService.saveTranslatedFields(receivedRole, updatedDto.getTranslatedFields());
        // if we deleteVersion RoleFeature from front, deleteVersion it on database and add new RoleFeatures
        receivedRole.setFeatures(addRoleFeatures(receivedRole, updatedDto.getFeatures()));
        RoleFullDto updatedRole =  dtoConverter.convert(roleRepository.save(receivedRole), RoleFullDto::new);
        updatedRole.setTranslatedLabels(translateService.getFieldValues(receivedRole, Role.FIELD_LABEL));
        // save and return the new updated role
        return updatedRole;
    }

    /**
     * update role labels
     *
     * @param id
     * @param updateLabels
     * @return
     */
    @Override
    @Secured("ROLE_ADMIN:WRITE")
    public RoleFullDto updateRole(Long id, Map<Language, String> updateLabels) {
        Role receivedRole = roleRepository.getOne(id);
        receivedRole = roleRepository.save(receivedRole);
        translateService.saveFieldValues(receivedRole, Role.FIELD_LABEL, updateLabels);

        return dtoConverter.convert(receivedRole, RoleFullDto::new);
    }

    /**
     * Get roleFullDto by Id
     *
     * @param id
     * @return
     */
    @Override
    @Secured("ROLE_ADMIN:READ")
    public RoleFullDto getRole(Long id) {
        Role role = roleRepository.getOne(id);
        if (role != null) {
            Map<Language, String> translatedLabels = translateService.getFieldValues(role,  Role.FIELD_LABEL);
            Optional<List<RoleFeature>> listFeatures = roleFeatureRepository.findByRoleId(id);
            listFeatures.ifPresent(roleFeatures -> role.setFeatures(roleFeatures.stream().collect(Collectors.toSet())));
            RoleFullDto result = dtoConverter.convert(role, RoleFullDto::new);
            result.setTranslatedLabels(translatedLabels);
            return result;
        }
        // FIXME : soulever une expcetion (ALEX API ERROR) : ROLE NOT FOUND
        return null;
    }

    /**
     * @param locale
     * @return all Roles in dataBase only with the given isoCode
     */
    @Override
    @Secured("ROLE_ADMIN:READ")
    public List<RoleDto> getAllRolesWithAllAirbusEntities(Locale locale) {
		Specification<Role> spec = locale == null ? null
				: Specification.where(RoleSpecification.filterByIsoCode(Language.languageFor(locale)));
        List<Role> rolesWithSpec = roleRepository.findAll(spec);
        return dtoConverter.convert(rolesWithSpec, RoleDto::new);
    }

    /**
     * @param roleId
     * @return boolean indicating if the role has been revoked
     */
    @Override
    @Secured("ROLE_ADMIN:WRITE")
    public boolean revokeRole(Long roleId) {
        return roleRepository.revokeRole(roleId) > 0;
    }

    /**
     * @param roleId
     * @return boolean indicating if the role has been revoked
     */
    @Override
    @Secured("ROLE_ADMIN:WRITE")
    public boolean revokeRole(Long roleId, Long airbusEntityId) {
        return roleRepository.revokeRole(roleId, airbusEntityId) > 0;
    }

    private Set<RoleFeature> addRoleFeatures(Role role, List<FeatureRightDto> featureRightDtos) {
        List<FeatureCode> featureCodes = featureRightDtos.stream().map(FeatureRightDto::getCode).collect(Collectors.toList());
        role.getFeatures().stream().forEach(userFeature -> {
            if (!featureCodes.contains(userFeature.getCode())) {
                roleFeatureRepository.delete(role.getId(), userFeature.getCode());
            }
        });

        Set<RoleFeature> roleFeatures = new HashSet<>();
        featureRightDtos.stream().forEach(featureRightDto -> {
            Optional<RoleFeature> roleFeatureOpt = roleFeatureRepository.findRoleFeature(role.getId(), featureRightDto.getCode());
            if (roleFeatureOpt.isPresent()) {
                RoleFeature roleFeature = roleFeatureOpt.get();
                roleFeature.setRightLevel(featureRightDto.getRightLevel());
                roleFeatures.add(roleFeature);
            } else {
                RoleFeature uf = new RoleFeature(role.getId(), featureRightDto.getCode(), featureRightDto.getRightLevel());
                roleFeatures.add(uf);
            }
        });
        return roleFeatures;
    }
}
